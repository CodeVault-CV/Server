package com.example.algoproject.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.*;

@Slf4j
@Component
public class ReadMeUtil {
    /* 리드미 생성 메소드 */
    public MultipartFile makeReadMe(String header, String content) throws IOException {

        File readMe = new File(System.getProperty("user.dir") + "/src/main/README.md");
        log.info(System.getProperty("user.dir") + "/src/main/README.md");
        try {
            if (readMe.createNewFile()) {
                log.info("ReadMe : File created... ");

                BufferedWriter writer = new BufferedWriter(new FileWriter(readMe, true));

                writer.write("# " + header.strip()); // h1
                writer.newLine();
                writer.write("## :pray: Review"); //h2
                writer.newLine();
                writer.write(content);

                writer.flush(); // 버퍼의 남은 데이터를 모두 쓰기
                writer.close();

            } else { // 파일 이미 존재하면 생성 안됨
                log.info("ReadMe : File already exists... ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(readMe.toPath()), false, readMe.getName(), (int) readMe.length(), readMe.getParentFile());
        try {
            InputStream input = new FileInputStream(readMe);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

        return multipartFile;
    }

    /* 로컬에 생성된 README.md 파일 삭제 */
    public void removeReadMe(MultipartFile readMe) {
        Path path = Paths.get(System.getProperty("user.dir") + "/src/main/" + readMe.getOriginalFilename());

        try {
            // 파일 삭제
            Files.delete(path);
            log.info("delete readme : " + "readme remove..");
        } catch (NoSuchFileException e) {
            System.out.println("삭제하려는 파일이 없습니다..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
