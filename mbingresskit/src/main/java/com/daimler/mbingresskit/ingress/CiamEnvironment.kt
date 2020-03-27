package com.daimler.mbingresskit.ingress

enum class CiamEnvironment : Endpoint, CiamIds {
    INT {
        override val openIdBaseUrl: String
            get() = "https://api-test.secure.mercedes-benz.com"
        override val webLoginBaseUrl: String
            get() = "https://login-test.secure.mercedes-benz.com"
        override val redirectUrl: String
            get() = "https://ciamdemo-int.daimler.com"
        override val clientId: String
            get() = "4d9e2215-4ff5-4019-9582-9b0a9bcf0c9f"
        override val appId: String
            get() = "CIAMAPP.INT_CIAM"
    },
    PROD {
        override val openIdBaseUrl: String
            get() = "https://api.secure.mercedes-benz.com/"
        override val webLoginBaseUrl: String
            get() = "https://login.secure.mercedes-benz.com"
        override val redirectUrl: String
            get() = "https://ciamdemo.daimler.com"
        override val clientId: String
            get() = "74f56a04-cbc4-425a-8663-dcfcfecd0c2b"
        override val appId: String
            get() = "CIAMAPP.PROD"
    }
}
