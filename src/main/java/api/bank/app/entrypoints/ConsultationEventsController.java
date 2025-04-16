package api.bank.app.entrypoints;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/queries")
@CrossOrigin
@AllArgsConstructor
public class ConsultationEventsController implements ConsultationEventsResource {

}
