// package com.lolgap.project.controllers;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.lolgap.project.models.Account;
// import com.lolgap.project.repositories.AccountRepository;
// import com.lolgap.project.services.RiotAccountService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.boot.test.mock.mockito.MockBean;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// public class AccountControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private AccountRepository accountRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @SuppressWarnings("removal")
//     @MockBean
//     private RiotAccountService riotAccountService;

//     @BeforeEach
//     void setUp() {
//         accountRepository.deleteAll();
//     }

//     @Test
//     void whenRegisterWithValidData_thenSuccess() throws Exception
//     {
//         // TEST DATA
//         Account newAccount = new Account();
//         newAccount.setUsername("testUser");
//         newAccount.setPassword("testPassword");
//         newAccount.setRiotGameName("TestGame");
//         newAccount.setRiotTagLine("TEST123");

//         // MOCK RIOT SERVICE
//         when(riotAccountService.enrichAccountWithRiotInfo(any(Account.class))).thenAnswer(invocation -> 
//         {
//             Account account = invocation.getArgument(0);
//             account.setRiotPuuid("test-puuid");
//             account.setSummonerId("test-summoner-id");
//             account.setAccountId("test-account-id");
//             return account;
//         });

//         mockMvc.perform(post("/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(newAccount)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.username").value("testUser"))
//                 .andExpect(jsonPath("$.riotPuuid").value("test-puuid"));
//     }

//     @Test
//     void whenRegisterWithExistingUsername_thenBadRequest() throws Exception 
//     {
//         // CREATE EXISTING ACCOUNT
//         Account existingAccount = new Account();
//         existingAccount.setUsername("testUser");
//         existingAccount.setPassword(passwordEncoder.encode("password"));
//         existingAccount.setRiotGameName("ExistingGame");
//         existingAccount.setRiotTagLine("EX123");
//         accountRepository.save(existingAccount);

//         // TENTER DE CRÉER UN COMPTE AVEC LE MÊME USERNAME
//         Account newAccount = new Account();
//         newAccount.setUsername("testUser");
//         newAccount.setPassword("differentPassword");
//         newAccount.setRiotGameName("NewGame");
//         newAccount.setRiotTagLine("NEW123");

//         mockMvc.perform(post("/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(newAccount)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string("Username already taken"));
//     }

//     @Test
//     void whenLoginWithValidCredentials_thenSuccess() throws Exception
//     {
//         // CRÉER UN COMPTE POUR LE TEST
//         Account account = new Account();
//         account.setUsername("loginTest");
//         account.setPassword(passwordEncoder.encode("password123"));
//         account.setRiotGameName("LoginGame");
//         account.setRiotTagLine("LOG123");
//         accountRepository.save(account);

//         // TEST LOGIN
//         Account loginRequest = new Account();
//         loginRequest.setUsername("loginTest");
//         loginRequest.setPassword("password123");

//         mockMvc.perform(post("/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(loginRequest)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())));
//     }

//     @Test
//     void whenLoginWithInvalidCredentials_thenUnauthorized() throws Exception
//     {
//         Account loginRequest = new Account();
//         loginRequest.setUsername("wrongUser");
//         loginRequest.setPassword("wrongPassword");

//         mockMvc.perform(post("/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(loginRequest)))
//                 .andExpect(status().isUnauthorized());
//     }
// } 