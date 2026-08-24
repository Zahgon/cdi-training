package at.gepardec.training.cdi.basic.inject;

import at.gepardec.training.cdi.Models;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Field injection is deliberate here: it leaves the implicit public no-arg constructor in
 * place, so {@code new InjectModel()} still compiles for {@link InjectFieldController} and
 * still yields an instance whose {@code models} reference is null.
 */
@Component
@RequestScope
public class InjectModel {

    @Autowired
    private Models models;

    public void setForView(String name) {
        models.put("name", "Congratulations '" + name + "', your fix works");
    }
}
