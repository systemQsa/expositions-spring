package com.myproject.expo.expositions.controller.util;

import com.myproject.expo.expositions.TestRunner;
import com.myproject.expo.expositions.build.Build;
import com.myproject.expo.expositions.build.ExpoBuild;
import com.myproject.expo.expositions.dto.ExpoDto;
import com.myproject.expo.expositions.entity.Exposition;
import com.myproject.expo.expositions.entity.Statistic;
import com.myproject.expo.expositions.generator.TestEntity;
import com.myproject.expo.expositions.service.ExpoService;
import com.myproject.expo.expositions.service.HallService;
import com.myproject.expo.expositions.service.ThemeService;
import com.myproject.expo.expositions.validator.Validate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static com.myproject.expo.expositions.generator.TestEntity.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExpoUtilControllerTest extends TestRunner {
    @MockBean
    private ExpoService expoService;
    @Mock
    private HallService hallService;
    @Mock
    private ThemeService themeService;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private Model model;
    @Autowired
    @Qualifier("expoBuild")
    private Build<ExpoDto, Exposition> build;
    @InjectMocks
    private ExpoUtilController expoUtilController;
    private final Exposition exposition = new Exposition();
    @Mock
    @Qualifier("expoBuild")
    private Build<ExpoDto, Exposition> buildExpo;

    @Before
    public void init() {
        initializeExpo();

    }

    private void initializeExpo() {
        exposition.setIdExpo(17L);
        exposition.setName("sky-champions");
        exposition.setExpoDate(LocalDate.now());
        exposition.setExpoTime(LocalTime.of(13, 30));
        exposition.setPrice(new BigDecimal(300));
        exposition.setStatusId(1);
        exposition.setStatistic(new Statistic(1L, 20L, 450L));
        exposition.setHalls(Collections.singleton(HallTest.hall1));
        exposition.setTheme(ThemeTest.theme1);
    }

    @Test
    public void getPageToAddExpo() {
        when(hallService.getAll()).thenReturn(List.of(HallTest.hall1, HallTest.hall2));
        when(themeService.getAll()).thenReturn(List.of(ThemeTest.theme1, ThemeTest.theme2));
        assertThat(expoUtilController.getPageToAddExpo(model)).isNotEmpty();
    }

    @Test
    public void addExpo() {
        ExpoDto expoDto1 = ExpoTest.expoDto1;
        when(expoService.addExpo(expoDto1, List.of(1L, 2L))).thenReturn(build.toModel(expoDto1));
        when(buildExpo.toModel(expoDto1)).thenReturn(exposition);
        assertThat(expoUtilController.addExpo(expoDto1, bindingResult, model)).isNotEmpty();
    }

    @Test
    public void getAllHalls() {
        when(hallService.getAll()).thenReturn(List.of(HallTest.hall1, HallTest.hall2));
        assertThat(expoUtilController.getAllHalls()).hasSizeGreaterThan(0);

    }

    @Test
    public void getAllThemes() {
        when(themeService.getAll()).thenReturn(List.of(ThemeTest.theme1, ThemeTest.theme2));
        assertThat(expoUtilController.getAllThemes()).hasSizeGreaterThan(0);
    }

}