package com.zephyr.springboottemplate.controller;

import cn.hutool.core.io.FileUtil;
import com.zephyr.springboottemplate.common.BaseResponse;
import com.zephyr.springboottemplate.common.ErrorCode;
import com.zephyr.springboottemplate.constant.FileConstant;
import com.zephyr.springboottemplate.exception.BusinessException;
import com.zephyr.springboottemplate.manager.CosManager;
import com.zephyr.springboottemplate.model.dto.file.UploadFileRequest;
import com.zephyr.springboottemplate.model.entity.User;
import com.zephyr.springboottemplate.model.enums.FileUploadBizEnum;
import com.zephyr.springboottemplate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;

/**
 * 文件上传控制器
 *
 * <p>
 * 负责处理文件上传请求，并将文件存储到腾讯云 COS。
 * </p>
 */
@RestController
@RequestMapping("/file")
@Slf4j
@Tag(name = "文件管理", description = "提供文件上传和管理的相关接口")
public class FileController {

    /**
     * 用户服务，用于获取当前登录用户信息。
     */
    @Resource
    private UserService userService;

    /**
     * 腾讯云 COS 管理器，用于文件上传到对象存储。
     */
    @Resource
    private CosManager cosManager;

    /**
     * 文件上传接口
     *
     * <p>
     * 接收客户端上传的文件，校验文件合法性后，将文件存储到腾讯云 COS，并返回文件的访问地址。
     * </p>
     *
     * @param multipartFile     客户端上传的文件
     * @param uploadFileRequest 包含业务类型等参数的文件上传请求对象
     * @param request           HTTP 请求对象，用于获取当前用户登录信息
     * @return 上传成功后返回文件的 URL
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传", description = "根据业务类型上传文件到对象存储，并返回文件的访问地址")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        // 获取上传业务类型，并验证其合法性
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验文件
        validFile(multipartFile, fileUploadBizEnum);

        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 生成文件的唯一路径
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);

        File file = null;
        try {
            // 创建临时文件并将上传的文件写入
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);

            // 将文件上传到腾讯云 COS
            cosManager.putObject(filepath, file);

            // 返回文件的访问地址
            return new BaseResponse<>(0, FileConstant.COS_HOST + filepath, "ok");
        } catch (Exception e) {
            log.error("file upload error, filepath: {}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            // 删除临时文件
            if (file != null) {
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath: {}", filepath);
                }
            }
        }
    }

    /**
     * 校验上传文件的合法性
     *
     * <p>
     * 根据业务类型对文件大小和格式进行校验。
     * </p>
     *
     * @param multipartFile    客户端上传的文件
     * @param fileUploadBizEnum 文件上传的业务类型枚举
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 获取文件大小
        long fileSize = multipartFile.getSize();
        // 获取文件后缀名
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_MB = 1024 * 1024L;

        // 针对用户头像的特殊校验
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_MB) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像大小不能超过 1MB");
            }
            if (!Arrays.asList("jpg", "jpeg", "png", "svg", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像只支持 jpg、jpeg、png、svg、webp 格式");
            }
        }
    }
}