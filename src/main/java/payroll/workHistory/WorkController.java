package payroll.workHistory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import payroll.common.dto.ResultDTO;
import payroll.workHistory.dto.WorkRequestDTO;
import payroll.workHistory.dto.WorkResponseDTO;
import payroll.workHistory.dto.WorkSearchDTO;
import payroll.workHistory.dto.WorkUpdateRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @GetMapping("/work-history")
    public ResponseEntity<List<WorkResponseDTO>> findWorkAll(@ModelAttribute WorkSearchDTO searchDTO){
        return ResponseEntity.ok(workService.findWork(searchDTO));
    }
}
