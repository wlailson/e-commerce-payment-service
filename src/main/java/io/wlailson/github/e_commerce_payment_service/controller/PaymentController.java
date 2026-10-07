package io.wlailson.github.e_commerce_payment_service.controller;

import io.wlailson.github.e_commerce_payment_service.dto.PaymentRequestDTO;
import io.wlailson.github.e_commerce_payment_service.dto.PaymentResponseDTO;
import io.wlailson.github.e_commerce_payment_service.service.AuthenticatedUser;
import io.wlailson.github.e_commerce_payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/payments")
@RestController
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Operações para criação e consulta de pagamentos.")
public class PaymentController {

    private final AuthenticatedUser authenticatedUser;
    private final PaymentService service;

    @PostMapping
    @Operation(
            summary = "Criar pagamento",
            description = "Cria um pagamento para o usuário autenticado. O identificador do usuário é obtido do token JWT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pagamento criado.",
                    headers = @Header(name = "Location", description = "URI do pagamento criado.")
            ),
            @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido."),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido.")
    })
    public ResponseEntity<PaymentResponseDTO> insertPayment(@RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = service.insertPayment(authenticatedUser.getUserId(), request);
        URI location = URI.create("/payments/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{paymentId}")
    @Operation(
            summary = "Consultar pagamento",
            description = "Retorna um pagamento pertencente ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado."),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido.")
    })
    public ResponseEntity<PaymentResponseDTO> findPaymentById(
            @Parameter(description = "Identificador do pagamento.", example = "501")
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(service.findPaymentById(authenticatedUser.getUserId(), paymentId));
    }

    @GetMapping
    @Operation(
            summary = "Listar pagamentos",
            description = "Lista de forma paginada os pagamentos do usuário autenticado. Use os parâmetros page "
                    + "(índice iniciado em 0), size e sort para definir a página e a ordenação."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de pagamentos retornada."),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido.")
    })
    public ResponseEntity<Page<PaymentResponseDTO>> findAllPayments(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAllPayments(authenticatedUser.getUserId(), pageable));
    }

}
