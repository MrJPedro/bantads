package com.bantads.conta_service.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.bantads.conta_service.repository.comando"],
    entityManagerFactoryRef = "commandEntityManager",
    transactionManagerRef = "commandTransactionManager"
)
class ComandoConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.command")
    fun commandDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Primary
    @Bean
    fun commandEntityManager(
        @Qualifier("commandDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.setPackagesToScan("com.bantads.conta_service.entity")
        em.jpaVendorAdapter = HibernateJpaVendorAdapter()
        return em
    }

    @Primary
    @Bean
    fun commandTransactionManager(
        @Qualifier("commandEntityManager") commandEntityManager: LocalContainerEntityManagerFactoryBean
    ): JpaTransactionManager {
        return JpaTransactionManager(commandEntityManager.`object`!!)
    }
}