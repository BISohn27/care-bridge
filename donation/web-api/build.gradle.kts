dependencies {
    implementation(project(":donation:donation-domain"))
    implementation(project(":donation:donation-infrastructure"))
    implementation(project(":donation:donation-producer"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-redis")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
}