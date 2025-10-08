plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "care-bridge"

include(
    "common:common-domain",
    "donation:donation-domain",
    "donation:infrastructure",
    "donation:web-api"
)

project(":common:common-domain").projectDir = file("common/domain")
project(":donation:donation-domain").projectDir = file("donation/domain")