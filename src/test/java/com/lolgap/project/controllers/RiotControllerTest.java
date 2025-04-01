// package com.lolgap.project.controllers;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.lolgap.project.dto.RiotAccountDTO;
// import com.lolgap.project.dto.LeagueAccountDTO;
// import com.lolgap.project.models.Account;
// import com.lolgap.project.repositories.AccountRepository;
// import com.lolgap.project.services.RiotAccountService;
// import com.lolgap.project.security.JwtUtil;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// public class RiotControllerTest 
// {

//     @Autowired
//     private MockMvc mockMvc;

//     @SuppressWarnings("unused")
//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private AccountRepository accountRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Autowired
//     private JwtUtil jwtUtil;

//     @SuppressWarnings("removal")
//     @MockBean
//     private RiotAccountService riotAccountService;

//     private String jwtToken;

//     @BeforeEach
//     void setUp()
//     {
//         accountRepository.deleteAll();
        
//         Account testAccount = new Account();
//         testAccount.setUsername("testUser");
//         testAccount.setPassword(passwordEncoder.encode("testPassword"));
//         testAccount.setRiotGameName("TestGame");
//         testAccount.setRiotTagLine("TEST123");
//         testAccount.setRiotPuuid("test-puuid");
//         testAccount.setSummonerId("test-summoner-id");
//         testAccount.setAccountId("test-account-id");
//         accountRepository.save(testAccount);

//         jwtToken = "Bearer " + jwtUtil.generateToken("testUser");
//     }

//     @Test
//     void whenGetAccountWithValidToken_thenSuccess() throws Exception
//     {
//         RiotAccountDTO riotAccountDTO = new RiotAccountDTO();
//         riotAccountDTO.setPuuid("test-puuid");
//         riotAccountDTO.setGameName("TestGame");
//         riotAccountDTO.setTagLine("TEST123");

//         when(riotAccountService.getRiotAccountInfo(anyString(), anyString()))
//             .thenReturn(riotAccountDTO);

//         mockMvc.perform(get("/api/riot/account")
//                 .header("Authorization", jwtToken))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.puuid").value("test-puuid"))
//                 .andExpect(jsonPath("$.gameName").value("TestGame"))
//                 .andExpect(jsonPath("$.tagLine").value("TEST123"));
//     }

//     @Test
//     void whenGetAccountWithoutToken_thenUnauthorized() throws Exception
//     {
//         mockMvc.perform(get("/api/riot/account"))
//                 .andExpect(status().isForbidden());
//     }

//     @Test
//     void whenGetLeagueWithValidToken_thenSuccess() throws Exception
//     {
//         LeagueAccountDTO leagueAccountDTO = new LeagueAccountDTO();
//         leagueAccountDTO.setId("test-summoner-id");
//         leagueAccountDTO.setAccountId("test-account-id");
//         leagueAccountDTO.setPuuid("test-puuid");
//         leagueAccountDTO.setName("TestGame");
//         leagueAccountDTO.setProfileIconId(1);
//         leagueAccountDTO.setSummonerLevel(30);

//         when(riotAccountService.getLeagueAccountByPuuid(anyString()))
//             .thenReturn(leagueAccountDTO);

//         mockMvc.perform(get("/api/riot/league")
//                 .header("Authorization", jwtToken))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value("test-summoner-id"))
//                 .andExpect(jsonPath("$.name").value("TestGame"))
//                 .andExpect(jsonPath("$.summonerLevel").value(30));
//     }

//     @Test
//     void whenGetLeagueWithInvalidPuuid_thenNotFound() throws Exception
//     {
//         when(riotAccountService.getLeagueAccountByPuuid(anyString()))
//             .thenReturn(null);

//         mockMvc.perform(get("/api/riot/league")
//                 .header("Authorization", jwtToken))
//                 .andExpect(status().isNotFound())
//                 .andExpect(content().string("Compte League of Legends non trouvé"));
//     }
// } 