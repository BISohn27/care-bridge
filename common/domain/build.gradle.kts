dependencies {
    compileOnly("jakarta.persistence:jakarta.persistence-api")
    compileOnly("org.springframework.data:spring-data-jpa")

    implementation("org.springframework:spring-aop")
    implementation("org.aspectj:aspectjweaver")

    testImplementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")
}