package tamk.tehtava.commands;

import picocli.CommandLine.Command;
import tamk.tehtava.datamodel.RuleBasedEvent;
import tamk.tehtava.datamodel.VerbalRule;
import tamk.tehtava.datamodel.Category;

@Command(name = "listruleevents", description = "Lists rule-based events for demonstration")
public class ListRuleEvents implements Runnable {

    @Override
    public void run() {
        // Create a rule-based event for Thanksgiving (U.S.A.)
        RuleBasedEvent thanksgiving = new RuleBasedEvent(
                VerbalRule.parse("fourth thursday in november"),
                "Thanksgiving (U.S.A.)",
                new Category("usa", "holiday")
        );

        // Create a rule-based event for Mother's Day in Finland
        RuleBasedEvent mothersDayFinland = new RuleBasedEvent(
                VerbalRule.parse("second sunday in may"),
                "Äitienpäivä (Suomi)",
                new Category("finland", "flagday")
        );

        // Print the rule-based events.
        System.out.println("Rule-based events:");
        System.out.println(thanksgiving);
        System.out.println(mothersDayFinland);
    }
}
