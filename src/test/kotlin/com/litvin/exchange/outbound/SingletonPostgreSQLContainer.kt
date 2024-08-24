package com.litvin.exchange.outbound

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import org.testcontainers.containers.PostgreSQLContainer

object SingletonPostgreSQLContainer {
    private val dbPort: Int = 64971
    private val exposedPort: Int = 5432

    val instance: PostgreSQLContainer<*> by lazy {
        PostgreSQLContainer("postgres:latest").apply {
            withDatabaseName("testdb")
            withUsername("user")
            withPassword("password")
            withExposedPorts(exposedPort)
            withCreateContainerCmdModifier {
                it.withHostConfig(
                    HostConfig().withPortBindings(
                        PortBinding(
                            Ports.Binding.bindPort(dbPort),
                            ExposedPort(exposedPort),
                        ),
                    ),
                )
            }
            start()
        }
    }
}
