package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.BiMqConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.dto.chart.RequestDto;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import com.yupi.springbootinit.utils.ChartUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static com.yupi.springbootinit.utils.ChartConstant.GEN_CONTENT_SPLITS;
import static com.yupi.springbootinit.utils.ChartConstant.GEN_ITEM_NUM;


@Component
@Slf4j
public class BiMessageConsumer {
    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    /**
     * 指定程序监听的消息队列和确认机制
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
    private void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message={}", message);
        if (StringUtils.isBlank(message)) {
            // 消息为空，则拒绝掉消息
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接受到的消息为空");
        }
        // 获取到图表的id
        long chartId = Long.parseLong(message);
        // 从数据库中取出id
        Chart chart = chartService.getById(chartId);
        if (chart == null) {
            // 将消息拒绝
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图表为空");
        }
        // 等待-->执行中--> 成功/失败
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean updateChartById = chartService.updateById(updateChart);
        if (!updateChartById) {
            // 将消息拒绝
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Chart updateChartFailed = new Chart();
            updateChartFailed.setId(chart.getId());
            updateChartFailed.setStatus("failed");
            chartService.updateById(updateChartFailed);
            handleSaveError(chart.getId(), "更新图表·执行中状态·失败");
            return;
        }
        RequestDto requestDto = new RequestDto();
        requestDto.setMessage(buildUserInput(chart));
        // 调用AI
        String chartResult = aiManager.doChatGPT(requestDto);

        // 解析内容
        String[] splits = chartResult.split(GEN_CONTENT_SPLITS);
        if (splits.length > GEN_ITEM_NUM) {
            //throw new BusinessException(ErrorCode.SYSTEM_ERROR, "");
            handleSaveError(chart.getId(), "AI生成错误");
            return;
        }
        // 生成前的内容
        String preGenChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 生成后端检验
        String validGenChart = ChartUtils.getValidGenChart(preGenChart);

        // 生成的最终结果-成功
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(preGenChart);
        //updateChartResult.setGenChart(validGenChart);
        updateChartResult.setGenResult(genResult);
        updateChartResult.setStatus("succeed");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            // 将消息拒绝
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Chart updateChartFailed = new Chart();
            updateChartFailed.setId(chart.getId());
            updateChartFailed.setStatus("failed");
            chartService.updateById(updateChartFailed);
            handleSaveError(chart.getId(), "更新图表·成功状态·失败");
        }

        // 成功，则确认消息
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 构建用户的输入信息
     *
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用，作为分隔符}\n" +
                "请根据以上内容，帮我生成数据分析结论和可视化图表代码\n" +
                "3）控制输出格式（便于AI返回的内容能够更方便地为我们所用）\n" +
                "Prompt预设：\n" +
                "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象的json格式代码，一定带上双引号，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
        //long biModelId = 1659171950288818178L;
        //构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append(prompt).append("\n");
        userInput.append("分析需求：").append("\n");
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String chartData = chart.getChartData();
        userInput.append("分析需求：").append("\n");
        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(chartData).append("\n");
        return userInput.toString();
    }
    private void handleSaveError(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,execMessage);
        }
    }
}
