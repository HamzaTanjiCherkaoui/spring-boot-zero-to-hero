package com.thirdstart.spring.zerotohero

import com.thirdstart.spring.zerotohero.domain.Contact
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Sometimes things go wrong. You look for something that isn't there. You look for something you're not supposed
 * to find. This spec covers real-world GET operations on our ContactController to make sure we're handling the
 * madness that is real life.
 */
@SpringBootTest(classes = ZeroToHeroConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactSingularGetSpec extends AbstractContactApiSpec {

    def "We can get a contact with a GET to /contacts/:id"() {
        setup:
        Long contactId = createARandomContact().id

        when:
        ResponseEntity<Contact> response = service.get("/contacts/${contactId}", Contact)

        then:
        response.statusCode == HttpStatus.OK
        response.body.id == contactId
    }

    def "If we get a contact with an invalid id, we get a 404"() {
        setup:
        Long contactId = -1 // because example, ok? and have you _ever_ encountered an id of -1?

        when:
        ResponseEntity response = service.get("/contacts/${contactId}")

        then:
        response.statusCode == HttpStatus.NOT_FOUND
    }

}