dependencies {
    implementation(project(":common:common-domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-redis")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    runtimeOnly("com.h2database:h2")
    implementation("com.mysql:mysql-connector-j")
    implementation("io.lettuce:lettuce-core")
}