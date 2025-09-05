dependencies {
    implementation(project(":common:common-domain"))

    compileOnly("jakarta.persistence:jakarta.persistence-api")
    compileOnly("org.springframework.data:spring-data-jpa")
    implementation("org.springframework:spring-core")
}