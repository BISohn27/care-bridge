plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "care-bridge"

include(
    "common:common-domain",
    "payment:payment-domain",
    "payment:infrastructure",
)

project(":common:common-domain").projectDir = file("common/domain")
project(":payment:payment-domain").projectDir = file("payment/domain")