package com.litvin.exchange.outbound

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Scope
import liquibase.changelog.ChangeLogParameters
import liquibase.changelog.visitor.ChangeExecListener
import liquibase.command.CommandScope
import liquibase.command.core.UpdateCommandStep
import liquibase.command.core.helpers.ChangeExecListenerCommandStep
import liquibase.command.core.helpers.DatabaseChangelogCommandStep
import liquibase.command.core.helpers.DbUrlConnectionCommandStep
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.hibernate.dialect.PostgreSQLDialect
import org.hibernate.jpa.HibernatePersistenceProvider
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory
import org.springframework.jdbc.datasource.SingleConnectionDataSource
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.testcontainers.containers.PostgreSQLContainer
import java.io.OutputStream
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException
import java.util.UUID
import javax.sql.DataSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class PostgreSQLFixture {
    private val postgresContainer: PostgreSQLContainer<*> = SingletonPostgreSQLContainer.instance

    private lateinit var entityManager: EntityManager
    private lateinit var dataSource: DataSource

    protected lateinit var repositoryFactory: JpaRepositoryFactory

    @BeforeEach
    @Throws(SQLException::class)
    fun setupDatabase() {
        val uniqueDbName = "test_${UUID.randomUUID().toString().replace("-", "_")}"

        DriverManager
            .getConnection(
                postgresContainer.jdbcUrl,
                postgresContainer.username,
                postgresContainer.password,
            ).use { initialConnection ->
                initialConnection.createStatement().use { statement ->
                    statement.execute("CREATE DATABASE $uniqueDbName")
                }
            }

        setupEntityManager(uniqueDbName)
        runLiquibaseMigrations()
        repositoryFactory = JpaRepositoryFactory(entityManager)
    }

    @AfterAll
    fun tearDownDatabase() {
    }

    private fun setupEntityManager(databaseName: String) {
        val jdbcUrl = postgresContainer.jdbcUrl.replace("testdb", databaseName)
        val properties = createHibernateProperties()
        val scDataSource =
            SingleConnectionDataSource(jdbcUrl, postgresContainer.username, postgresContainer.password, true)
        dataSource = scDataSource

        val entityManagerFactory =
            LocalContainerEntityManagerFactoryBean().apply {
                this.persistenceUnitName = "my-test-unit"
                this.setJpaPropertyMap(properties)
                this.dataSource = scDataSource
                this.setPackagesToScan("com.litvin.exchange.outbound.db")
                this.jpaVendorAdapter = HibernateJpaVendorAdapter()
                this.setPersistenceProviderClass(HibernatePersistenceProvider::class.java)
                this.afterPropertiesSet()
            }
        entityManager = entityManagerFactory.createNativeEntityManager(emptyMap<String, Any>())
    }

    private fun createHibernateProperties(): Map<String, Any> =
        mapOf(
            "hibernate.dialect" to PostgreSQLDialect::class.java,
            "hibernate.connection.driver_class" to Driver::class.java,
            "hibernate.transaction.jta_platform" to SpringJtaPlatform::class.java,
            "hibernate.ddl-auto" to "none",
            "hibernate.show_sql" to false,
            "hibernate.format_sql" to false,
            "hibernate.physical_naming_strategy" to org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy::class.java,
        )

    private fun runLiquibaseMigrations() {
        dataSource.connection.use { connection ->
            val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
            val changeLogFile = "db/changelog/db.changelog-master.xml"

            val scopeObjects =
                mapOf(
                    Scope.Attr.database.name to database,
                    Scope.Attr.resourceAccessor.name to ClassLoaderResourceAccessor(),
                )

            Scope.child(scopeObjects) {
                val updateCommand =
                    CommandScope(*UpdateCommandStep.COMMAND_NAME).apply {
                        addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                        addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changeLogFile)
                        addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, Contexts().toString())
                        addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, LabelExpression().originalString)
                        addArgumentValue(
                            ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG,
                            null as ChangeExecListener?,
                        )
                        addArgumentValue(
                            DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS,
                            ChangeLogParameters(database),
                        )

                        setOutput(OutputStream.nullOutputStream())
                    }
                updateCommand.execute()
            }
        }
    }

    protected fun <T> withTransaction(block: EntityManager.() -> T): T {
        val transaction: EntityTransaction = entityManager.transaction
        return try {
            transaction.begin()
            val result = entityManager.block()
            transaction.commit()
            result
        } catch (ex: Exception) {
            if (transaction.isActive) {
                transaction.rollback()
            }
            throw ex
        }
    }
}
