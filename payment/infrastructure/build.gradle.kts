dependencies {
    implementation(project(":payment:payment-domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-redis")

    runtimeOnly("com.h2database:h2")
    implementation("com.mysql:mysql-connector-j")
    implementation("io.lettuce:lettuce-core")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
}