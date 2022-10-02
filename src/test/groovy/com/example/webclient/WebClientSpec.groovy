package com.example.webclient

import com.example.webclient.client.GithubClient
import com.example.webclient.client.GoogleClient
import com.example.webclient.utils.CoroutineTestUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class WebClientSpec extends Specification {

    @Shared
    String uuidRegex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}"

    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    @ClassRule
    @Shared
    WireMockRule google = new WireMockRule(30001)

    @ClassRule
    @Shared
    WireMockRule github = new WireMockRule(30002)

    @Autowired
    GoogleClient googleClient

    @Autowired
    GithubClient githubClient

    def setupSpec() {
        google.start()
        github.start()
    }

    def cleanup() {
        google.resetAll()
        github.resetAll()
    }

    def cleanupSpec() {
        google.stop()
        github.stop()
    }

    def "get google main"() {
        given:
        def expectedResult = [
            test: 123
        ]
        google.addStubMapping(
            get(urlEqualTo("/main"))
                .willReturn(okJson(objectMapper.writeValueAsString(expectedResult)))
                .build()
        )

        when:
        def result = CoroutineTestUtils.executeSuspendFun {
            googleClient.getMainPage(it)
        }

        then:
        result == expectedResult
        google.verify(1, getRequestedFor(urlPathEqualTo("/main")))
        0 * _
    }

    def "get github main"() {
        given:
        def expectedResult = [
            test: 123
        ]
        github.addStubMapping(
            get(urlEqualTo("/main"))
                .willReturn(okJson(objectMapper.writeValueAsString(expectedResult)))
                .build()
        )

        when:
        def result = CoroutineTestUtils.executeSuspendFun {
            githubClient.getMainPage(it)
        }

        then:
        result == expectedResult
        github.verify(1, getRequestedFor(urlPathEqualTo("/main")))
        0 * _
    }

    def "check web client header"() {
        given:
        def expectedResult = [
            test: 123
        ]
        github.addStubMapping(
            get(urlEqualTo("/main"))
                .withHeader("LOGGING-ID", matching(uuidRegex))
                .willReturn(okJson(objectMapper.writeValueAsString(expectedResult)))
                .build()
        )

        when:
        def result = CoroutineTestUtils.executeSuspendFun {
            githubClient.getMainPage(it)
        }

        then:
        result == expectedResult
        github.verify(1, getRequestedFor(urlPathEqualTo("/main")))
        0 * _
    }
}
