package com.thirdstart.spring.zerotohero.controllers

import com.thirdstart.spring.zerotohero.ApplicationConfiguration
import com.thirdstart.spring.zerotohero.domain.Contact
import com.thirdstart.spring.zerotohero.util.rest.exceptionhandling.ApiErrorInformation
import com.thirdstart.spring.zerotohero.util.rest.exceptionhandling.ApiErrorMessage
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Sometimes things go wrong. You look for something that isn't there. You look for something you're not supposed
 * to find. This spec covers real-world GET operations on our ContactController to make sure we're handling the
 * madness that is real life.
 */
@SpringBootTest(classes = ApplicationConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactValidationSpec extends AbstractContactControllerSpec {

    def setup() {
        withUser 'test_user'
    }

    def "We can save a valid contact with a POST to /contacts"() {
        when:
        ResponseEntity<Contact> response = service.post(
                '/contacts',
                new Contact(
                        firstName: "Chuck",
                        lastName: "Berry"
                )
        )

        then:
        response.statusCode == HttpStatus.OK
        response.body.id > 0
        response.body.firstName == "Chuck"
        response.body.lastName == "Berry"
    }

    def "POSTing an invalid contact returns a 400 - BAD REQUEST"() {
        when:
        ResponseEntity<ApiErrorInformation<Contact>> response = service.post(
                '/contacts',
                new Contact(
                        firstName: null,
                        lastName: null
                ),
                service.apiErrorInformationFor(Contact)
        )

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body instanceof ApiErrorInformation
        response.body.body instanceof Contact
        response.body.messages.size() >= 2

        ApiErrorMessage lastNameMessage = response.body.messages.find{ it.subject == 'lastName' }
        lastNameMessage != null
        lastNameMessage.code == 'NotNull.contact.lastName'
        lastNameMessage.message == 'This is required.'
    }

    def "POSTing a contact with the firstname 'Nope!' throws a handled exception"() {
        when:
        withUser('test_user')
        ResponseEntity<ApiErrorInformation<Contact>> response = service.post(
                '/contacts',
                new Contact(
                        firstName: 'Nope!',
                        lastName: 'Bobbo'
                ),
                service.apiErrorInformationFor(Contact)
        )

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        response.body instanceof ApiErrorInformation
        response.body.errorMessage == "API Error"
    }

}