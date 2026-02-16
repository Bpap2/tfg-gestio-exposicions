package cat.cccb.tfg.exposicions.importer;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminImportController {

    private final DumpImportService svc;

    public AdminImportController(DumpImportService svc) {
        this.svc = svc;
    }

    @PostMapping("/import-dump")
    public DumpImportService.ImportReport importDump(
            @RequestParam(defaultValue = "false") boolean dryRun,
            @RequestParam(defaultValue = "200") int pageSize,
            @RequestParam(defaultValue = "50") int flushEvery
    ) {
        return svc.importAll(dryRun, pageSize, flushEvery);
    }
}
