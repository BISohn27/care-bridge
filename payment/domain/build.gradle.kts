dependencies {
    implementation(project(":common:common-domain"))

    compileOnly("jakarta.persistence:jakarta.persistence-api")
    compileOnly("org.springframework.data:spring-data-jpa")
    compileOnly("org.springframework.data:spring-data-redis")
    implementation("org.springframework:spring-core")

    implementation("org.springframework:spring-aop")
    implementation("org.aspectj:aspectjweaver")
}