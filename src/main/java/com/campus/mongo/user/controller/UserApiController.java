package com.campus.mongo.user.controller;

import com.campus.mongo.user.dto.request.CreateUserRequest;
import com.campus.mongo.user.dto.request.EditUserRequest;
import com.campus.mongo.user.dto.response.UserResponse;
import com.campus.mongo.user.entity.UserDoc;
import com.campus.mongo.user.exception.ObjectIdParseException;
import com.campus.mongo.user.exception.UserNotFoundException;
import com.campus.mongo.user.repository.UserRepository;
import com.campus.mongo.user.routes.UserRoutes;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class UserApiController {
    private final UserRepository userRepository;

    @GetMapping("/")
    public UserDoc test(){
        UserDoc test = userRepository.findByFirstName("Test");

        UserDoc userDoc = UserDoc.builder()
                .id(new ObjectId())
                .firstName("Test")
                .lastName("Test")
                .build();
        userRepository.save(userDoc);

        return userDoc;
    }

    @PostMapping(UserRoutes.CREATE)
    public UserResponse create(@RequestBody CreateUserRequest request){
        UserDoc userDoc = UserDoc.builder()
                .id(new ObjectId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        userDoc = userRepository.save(userDoc);
        return UserResponse.of(userDoc);
    }

    @GetMapping(UserRoutes.BY_ID)
    public UserResponse byId(@PathVariable String id) throws ObjectIdParseException, UserNotFoundException {
        if(!ObjectId.isValid(id)) throw new ObjectIdParseException();
        UserDoc userDoc = userRepository
                .findById(new ObjectId(id))
                .orElseThrow(UserNotFoundException::new);
        return UserResponse.of(userDoc);
    }
    @GetMapping(UserRoutes.SEARCH)
    public List<UserResponse> search(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "0") Integer page){
        Pageable pageable = PageRequest.of(page, size);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());

        Example<UserDoc> example = Example.of(
                UserDoc.builder().firstName(query).lastName(query).build(),
                exampleMatcher);
        Page<UserDoc> userDocs = userRepository.findAll(example,  pageable);
        return userDocs.stream().map(UserResponse::of).collect(Collectors.toList());
    }

    @PutMapping(UserRoutes.EDIT)
    public UserResponse edit(@PathVariable String id, @RequestBody EditUserRequest request) throws ObjectIdParseException, UserNotFoundException {
        if (!ObjectId.isValid(id)) throw  new ObjectIdParseException();

        UserDoc userDoc = userRepository
                .findById(new ObjectId(id))
                .orElseThrow(UserNotFoundException::new);

        userDoc.setFirstName(request.getFirstName());
        userDoc.setLastName(request.getLastName());
        userRepository.save(userDoc);

        return UserResponse.of(userDoc);
    }

    @DeleteMapping(UserRoutes.DELETE)
    public String delete(@PathVariable String id) throws ObjectIdParseException {

        if (!ObjectId.isValid(id)) throw new ObjectIdParseException();

        userRepository.deleteById(new ObjectId(id));
        return HttpStatus.OK.name();

    }

}
