plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "care-bridge"

include(
    "common:common-domain",
    "donation:donation-domain",
    "donation:infrastructure",
    "donation:web-api",
    "donation:donation-batch",
    "donation:donation-producer",
    "donation:donation-consumer",
    "donationcase:donationcase-domain"
)

project(":common:common-domain").projectDir = file("common/domain")
project(":donation:donation-domain").projectDir = file("donation/domain")
project(":donationcase:donationcase-domain").projectDir = file("donationcase/domain")
project(":donation:donation-batch").projectDir = file("donation/batch")
project(":donation:donation-producer").projectDir = file("donation/producer")
project(":donation:donation-consumer").projectDir = file("donation/consumer")