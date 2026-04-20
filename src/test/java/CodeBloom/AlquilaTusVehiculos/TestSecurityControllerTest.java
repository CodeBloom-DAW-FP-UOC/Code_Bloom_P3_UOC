package CodeBloom.AlquilaTusVehiculos;

import CodeBloom.AlquilaTusVehiculos.controllers.TestSecurityController;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TestSecurityControllerTest {

    @Test
    void shouldReturnPublicOk() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new TestSecurityController())
                .build();

        mockMvc.perform(get("/public/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ruta pública OK"));
    }

    @Test
    void shouldReturnUserOk() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new TestSecurityController())
                .build();

        mockMvc.perform(get("/user/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ruta USER OK"));
    }

    @Test
    void shouldReturnAdminOk() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new TestSecurityController())
                .build();

        mockMvc.perform(get("/admin/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ruta ADMIN OK"));
    }
}