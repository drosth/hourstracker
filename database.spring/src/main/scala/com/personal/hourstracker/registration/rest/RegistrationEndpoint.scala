package com.personal.hourstracker.registration.rest

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.registration.service.RegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{ GetMapping, RequestMapping, RestController }

@RestController
@RequestMapping(path = Array("/api"))
class RegistrationEndpoint(@Autowired val registrationService: RegistrationService) {

  @GetMapping(path = Array("/registration"))
  def retrieveAllRegistrations(): Iterable[Registration] = {
    registrationService.listRegistrations()
  }
}

//class UserController(@Autowired val userService: UserService, @Autowired val dataSource: DataSource) {
//  @GetMapping(path = Array("/users"))
//  def getAllUsers(): Iterable[Users] = {
//    userService.listUsers
//  }
//  @GetMapping(path = Array("/users/{id}"))
//  def getUser(@PathVariable id: Long): Users = {
//    userService.getUser(id)
//  }
//  @PostMapping(path = Array("/users"))
//  def createUser(@RequestBody users: Users): ResponseEntity[Long] = {
//    val id = userService.createUser(users)
//    new ResponseEntity(id, new HttpHeaders, HttpStatus.CREATED)
//  }
//}
