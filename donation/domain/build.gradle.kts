dependencies {
    implementation(project(":common:common-domain"))
    implementation(project(":donationcase:donationcase-domain"))

    compileOnly("jakarta.persistence:jakarta.persistence-api")
    compileOnly("org.springframework.data:spring-data-jpa")

    implementation("org.springframework:spring-core")

    testImplementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")
}