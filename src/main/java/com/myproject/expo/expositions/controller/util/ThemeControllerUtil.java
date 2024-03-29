package com.myproject.expo.expositions.controller.util;

import com.myproject.expo.expositions.dto.ThemeDto;
import com.myproject.expo.expositions.entity.Theme;
import com.myproject.expo.expositions.exception.custom.ThemeException;
import com.myproject.expo.expositions.service.ThemeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The ThemeUtilController class do transfer operations for Theme Dto. Transfer to service layer
 */
@Component
public class ThemeControllerUtil {
    private static final Logger log = LogManager.getLogger(ThemeControllerUtil.class);
    private final ThemeService themeService;
    private List<Theme> list;
    private final ControllerHelper controllerHelper;

    public ThemeControllerUtil(ThemeService themeService, List<Theme> list, ControllerHelper controllerHelper) {
        this.themeService = themeService;
        this.list = list;
        this.controllerHelper = controllerHelper;
    }

    public Page<Theme> getAllThemes(Pageable pageable) {
        return themeService.getAll(controllerHelper.getPageableFromPageSize(pageable));
    }

    public int countNoRequiredPages(Pageable pageable) {
        list = themeService.getAll();
        return controllerHelper.countNoOfRequiredPagesForPage(list.size(), pageable.getPageSize());
    }

    public Theme saveTheTheme(ThemeDto themeDto) {
        return themeService.save(themeDto);
    }

    public boolean updateTheTheme(Long id, ThemeDto themeDto) {
        themeDto.setId(id);
        try {
            return themeService.update(themeDto) > 0;
        } catch (Exception e) {
            log.warn("Cannot update the theme {} with id {}. Such theme already exists in the system",
                    themeDto.getName(), themeDto.getId());
            throw new ThemeException("err.update_theme");
        }
    }

    public boolean deleteTheme(Long id) {
        try {
            return themeService.delete(id) > 0;
        } catch (Exception e) {
            throw new ThemeException("err.delete_theme");
        }
    }

}
