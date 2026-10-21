package com.marquesdev.park_api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingReportService {

    private final ResourceLoader resourceLoader;
    private final DataSource dataSource;


    private static final String JASPER_DIRETORIO = "classpath:/reports/";


    public byte[] generateReport(String cpf) {
        byte[] bytes = null;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("IMAGEM_DIRETORIO", JASPER_DIRETORIO);
        parameters.put("REPORT_LOCALE", Locale.forLanguageTag("pt-BR"));
        parameters.put("CPF", cpf);

        Resource resource = resourceLoader.getResource(JASPER_DIRETORIO.concat("parkingSpot.jasper"));
        try (InputStream stream = resource.getInputStream();
            Connection connection = dataSource.getConnection()){
            JasperPrint print = JasperFillManager.fillReport(stream, parameters, connection);
            bytes = JasperExportManager.exportReportToPdf(print);
        } catch (IOException | JRException | SQLException e) {
            log.error("Erro ao gerar relatório", e);
            throw new RuntimeException(e);
        }
        return bytes;

    }
}
