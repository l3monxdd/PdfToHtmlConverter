import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HTMLBlockRange {

    private String ownText;

    private Double xFirst;
    private Double xSecond;

    private Double yFirst;
    private Double ySecond;
}
