package com.myproject.expo.expositions.controller.util;

import com.myproject.expo.expositions.build.Build;
import com.myproject.expo.expositions.dto.ExpoDto;
import com.myproject.expo.expositions.entity.Exposition;
import com.myproject.expo.expositions.generator.TestEntity;
import com.myproject.expo.expositions.service.ExpoService;
import com.myproject.expo.expositions.service.HallService;
import com.myproject.expo.expositions.service.ThemeService;
import com.myproject.expo.expositions.validator.Validate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExpoUtilControllerTest {
    @MockBean
    private ExpoService expoService;
    @Mock
    private HallService hallService;
    @Mock
    private ThemeService themeService;
    @Mock
    @Qualifier("validateInput")
    private Validate validate;
    @Mock
    private Model model;
    @Autowired
    @Qualifier("expoBuild")
    private Build<ExpoDto, Exposition> build;
    @Autowired
    private ExpoUtilController expoUtilController;

    @Test
    public void getPageToAddExpo() {
        when(hallService.getAll()).thenReturn(List.of(TestEntity.HallTest.hall1, TestEntity.HallTest.hall2));
        when(themeService.getAll()).thenReturn(List.of(TestEntity.ThemeTest.theme1, TestEntity.ThemeTest.theme2));
        assertThat(expoUtilController.getPageToAddExpo(model)).isNotEmpty();
    }

    @Test
    public void addExpo() {
        ExpoDto expoDto1 = TestEntity.ExpoTest.expoDto1;
        when(validate.validateProperDate(expoDto1.getExpoDate())).thenReturn(true);
        when(validate.validateProperTime(expoDto1.getExpoTime())).thenReturn(true);
        when(expoService.getAll()).thenReturn(List.of(build.toModel(expoDto1)));
        when(expoService.addExpo(expoDto1, List.of(1L, 2L))).thenReturn(build.toModel(expoDto1));
        assertThat(expoUtilController.addExpo(expoDto1, List.of(1L, 2L), model)).isNotEmpty();
    }

    @Test
    public void getAllHalls() {
        when(hallService.getAll()).thenReturn(List.of(TestEntity.HallTest.hall1, TestEntity.HallTest.hall2));
        assertThat(expoUtilController.getAllThemes()).hasSizeGreaterThan(0);

    }

    @Test
    public void getAllThemes() {
        when(themeService.getAll()).thenReturn(List.of(TestEntity.ThemeTest.theme1, TestEntity.ThemeTest.theme2));
        assertThat(expoUtilController.getAllThemes()).hasSizeGreaterThan(0);
    }

}