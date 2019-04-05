import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class RTextElement {

    private String text;
    private StyleData style;
}
