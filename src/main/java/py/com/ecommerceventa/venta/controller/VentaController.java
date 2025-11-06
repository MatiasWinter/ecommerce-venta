package py.com.ecommerceventa.venta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.ecommerceventa.venta.DTO.VentaRequestDto;
import py.com.ecommerceventa.venta.DTO.VentaResponseDto;
import py.com.ecommerceventa.venta.service.VentaService;

@RestController
@RequestMapping("/venta")
@AllArgsConstructor
@Tag(name = "Ventas", description = "Operaciones relacionadas con el proceso de compra y generación de eventos") // 1. Agrupa el controlador
public class VentaController {

    private final VentaService ventaService;

    @Operation(
            summary = "Procesar una nueva venta",
            description = "Registra una nueva transacción de venta, descuenta el stock (asíncrono vía RabbitMQ), y genera el pago pendiente." // Descripción detallada
    )

    @ApiResponse(
            responseCode = "200",
            description = "Venta procesada exitosamente.",
            content = @Content(schema = @Schema(implementation = VentaResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Solicitud inválida (ej. stock insuficiente, ID de producto no encontrado, o datos de entrada erróneos).",
            // Reemplaza ErrorResponseDTO.class por tu clase de manejo de errores si la tienes
            content = @Content(schema = @Schema(implementation = Object.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor o fallo transaccional.",
            content = @Content(schema = @Schema(implementation = Object.class))
    )

    // 4. Mapeo del cuerpo de la petición (opcional, pero detallado)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Detalles de la compra: ID del cliente y la lista de productos con cantidad y precio unitario.",
            required = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = VentaRequestDto.class))
    )

    @PostMapping("/sell")
    public VentaResponseDto sell(
            @RequestBody VentaRequestDto ventaRequestDto
    ) {
        return ventaService.vender(ventaRequestDto);
    }
}
