package donatehub.controller;

import donatehub.domain.projections.*;
import donatehub.domain.response.ExceptionResponse;
import donatehub.service.donation.DonationService;
import donatehub.service.user.UserService;
import donatehub.service.withdraw.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistic")
@RequiredArgsConstructor
@Tag(name = "Statistic", description = "Statistic API")
public class StatisticController {
    private final UserService userService;
    private final DonationService donationService;
    private final WithdrawService withdrawService;

    @GetMapping("/user")
    @Operation(
            summary = "Umumiy statistika olish",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Umumiy statistikalar", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserStatistic.class)))
            }
    )
    public UserStatistic getUserStatistic() {
        return userService.getUsersStatistic();
    }

    @GetMapping("/profit")
    @Operation(
            summary = "Daromad statistikasini"
    )
    public ProfitStatistic getProfitStatistic() {
        return userService.getProfitStatistic();
    }

    @GetMapping("/withdraw")
    @Operation(
            summary = "Adming uchun withdraw statistikasini olish",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Withdraw statistikasi muvaffaqiyatli olingan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WithdrawStatistic.class))),
            }
    )
    public WithdrawStatistic getWithdrawStatistic() {
        return withdrawService.getStatistic();
    }

    @GetMapping("/withdraw/{streamerId}")
    @Operation(
            summary = "Streamer uchun withdraw statistikasini olish",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Withdraw statistikasi muvaffaqiyatli olingan", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WithdrawStatistic.class))),
            }
    )
    public WithdrawStatistic getWithdrawStatisticOfStreamer(@PathVariable Long streamerId) {
        return withdrawService.getStatisticOfStreamer(streamerId);
    }

    @GetMapping("/donation")
    @Operation(
            summary = "Ma'mur uchun statistikalarni olish",
            description = "Ushbu metod ma'mur uchun umumiy statistikalar ro'yxatini qaytaradi.",
            parameters = {
                    @Parameter(name = "days", description = "So'rov qilinayotgan statistikalar uchun kunlar soni", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Umumiy statistikalar", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DonationStatistic.class)))
            }
    )
    public DonationStatistic getDonationStatistic() {
        return donationService.getDonationStatistics();
    }

    @GetMapping("/donation/{streamerId}")
    @Operation(
            summary = "Streamer uchun statistikalarni olish",
            description = "Ushbu metod berilgan streamer uchun statistikalarni ro'yxatini qaytaradi.",
            parameters = {
                    @Parameter(name = "streamerId", description = "Streamer identifikatori", required = true),
                    @Parameter(name = "days", description = "So'rov qilinayotgan statistikalar uchun kunlar soni", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Streamer uchun statistikalar", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DonationStatistic.class))),
                    @ApiResponse(responseCode = "404", description = "Streamer topilmasa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
            }
    )
    public DonationStatistic getDonationStatisticOfStreamer(@PathVariable Long streamerId) {
        return donationService.getDonationStatisticsOfStreamer(streamerId);
    }

    @GetMapping
    public List<AdminStatisticByGraphic> adminStatisticByGraphic(@RequestParam int days){
        return userService.adminStatisticByGraphic(days);
    }

    @GetMapping("/{streamerId}")
    public List<UserStatisticByGraphic> userStatisticByGraphic(
            @RequestParam int days,
            @PathVariable Long streamerId
    ){
        return userService.userStatisticByGraphic(days, streamerId);
    }
}
