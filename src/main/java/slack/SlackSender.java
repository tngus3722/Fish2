package slack;

import domain.SlackAttachment;
import domain.SlackParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;


@Service
public class SlackSender {

    @Value("${slack_url}")
    private String slack_url;

    private RestTemplate restTemplate;
    private SlackParameter slackParameter;

    public SlackSender(){
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0,new StringHttpMessageConverter((Charset.forName("UTF-8"))));
        slackParameter = new SlackParameter();
        slackParameter.setChannel("#tngus");
        slackParameter.setUsername("알람");
    };

    public void noticePost(SlackAttachment slackAttachment){
        slackParameter.setText("noticePost");

        ArrayList<SlackAttachment> list = new ArrayList<SlackAttachment>();
        list.add(slackAttachment);

        slackParameter.setAttachments(list);

        restTemplate.postForObject(slack_url,slackParameter, String.class);
    }

    public void noticeError(SlackAttachment slackAttachment){
        slackParameter.setText("noticeError");

        ArrayList<SlackAttachment> list = new ArrayList<SlackAttachment>();
        list.add(slackAttachment);

        slackParameter.setAttachments(list);

        restTemplate.postForObject(slack_url,slackParameter, String.class);
    }
}
