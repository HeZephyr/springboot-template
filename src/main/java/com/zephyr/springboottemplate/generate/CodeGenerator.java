package com.zephyr.springboottemplate.generate;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.Writer;

/**
 * 代码生成器
 *
 * 功能描述：
 * 1. 通过模板文件和数据模型，自动生成代码文件。
 * 2. 支持生成常见的 Controller、Service、DTO、VO 等代码文件。
 *
 * 使用场景：
 * 1. 提高开发效率，减少重复性代码的编写。
 * 2. 确保生成的代码风格一致。
 *
 * 使用说明：
 * 1. 修改生成参数（如包名、数据名称等）。
 * 2. 根据需求选择生成逻辑（可以注释掉不需要的生成部分）。
 * 3. 运行程序生成对应的代码文件。
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public class CodeGenerator {

    /**
     * 主方法，用于执行代码生成任务
     *
     * @param args 命令行参数（未使用）
     * @throws TemplateException 模板处理异常
     * @throws IOException IO 异常
     */
    public static void main(String[] args) throws TemplateException, IOException {
        // 指定生成参数
        String packageName = "com.zephyr.springboottemplate"; // 包名
        String dataName = "用户评论";                   // 数据名称（中文描述）
        String dataKey = "userComment";               // 数据标识符（驼峰命名）
        String upperDataKey = "UserComment";          // 数据标识符（首字母大写）

        // 封装生成参数到数据模型
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("packageName", packageName);
        dataModel.put("dataName", dataName);
        dataModel.put("dataKey", dataKey);
        dataModel.put("upperDataKey", upperDataKey);

        // 获取当前项目路径
        String projectPath = System.getProperty("user.dir");

        // ======================= 生成 Controller =======================
        String inputPath = projectPath + File.separator + "src/main/resources/templates/TemplateController.java.ftl";
        String outputPath = String.format("%s/generator/controller/%sController.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        System.out.println("生成 Controller 成功，文件路径：" + outputPath);

        // ======================= 生成 Service =======================
        // 生成 Service 接口
        inputPath = projectPath + File.separator + "src/main/resources/templates/TemplateService.java.ftl";
        outputPath = String.format("%s/generator/service/%sService.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        System.out.println("生成 Service 接口成功，文件路径：" + outputPath);

        // 生成 Service 实现类
        inputPath = projectPath + File.separator + "src/main/resources/templates/TemplateServiceImpl.java.ftl";
        outputPath = String.format("%s/generator/service/impl/%sServiceImpl.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        System.out.println("生成 Service 实现类成功，文件路径：" + outputPath);

        // ======================= 生成 DTO 和 VO =======================
        // 生成 DTO 文件（请求对象）
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateAddRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%sAddRequest.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);

        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateQueryRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%sQueryRequest.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);

        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateEditRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%sEditRequest.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);

        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateUpdateRequest.java.ftl";
        outputPath = String.format("%s/generator/model/dto/%sUpdateRequest.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        System.out.println("生成 DTO 成功，文件路径：" + outputPath);

        // 生成 VO 文件（展示对象）
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/TemplateVO.java.ftl";
        outputPath = String.format("%s/generator/model/vo/%sVO.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel);
        System.out.println("生成 VO 成功，文件路径：" + outputPath);
    }

    /**
     * 根据模板生成文件
     *
     * @param inputPath  模板文件路径
     * @param outputPath 输出文件路径
     * @param model      数据模型
     * @throws IOException IO 异常
     * @throws TemplateException 模板处理异常
     */
    public static void doGenerate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // 初始化 FreeMarker 配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

        // 设置模板加载路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板字符集
        configuration.setDefaultEncoding("utf-8");

        // 加载模板文件
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        // 如果文件不存在，创建文件及父目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // 使用模板生成文件
        try (Writer out = new FileWriter(outputPath)) {
            template.process(model, out);
        }
    }
}