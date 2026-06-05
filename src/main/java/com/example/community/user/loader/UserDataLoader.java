package com.example.community.user.loader;

import com.example.community.user.dto.UserDTO;
import com.example.community.user.factory.UserFactory;
import com.example.community.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDataLoader implements ApplicationRunner {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserFactory userFactory;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/users.json");

        List<UserDTO> userDTOs = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<UserDTO>>() {}
        );

        userDTOs.stream()
                .map(userFactory::create)
                .forEach(userRepository::save);
    }
}
