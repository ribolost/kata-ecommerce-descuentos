package com.ribolost.pruebastecnicas.kataecommerce.shared.seed;

import com.ribolost.pruebastecnicas.kataecommerce.catalog.domain.Product.ProductCategory;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductDocument;
import com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Seed de arranque para el Catálogo: crea en MongoDB el listado de
 * ProductDocument si todavía no existe ninguno, para que el Motor de
 * Descuentos y el checkout tengan con qué operar en un ambiente recién
 * levantado (ver "Fuera de alcance / observaciones para el usuario" en
 * claude/scaffold-backend-notas.md, iteración 3).
 *
 * Incluye productos de categoría TECNOLOGIA (necesarios para ejercitar RN-01,
 * el descuento de categoría) junto con productos OTRO, con precios y stock
 * variados, incluyendo un producto sin stock disponible (stock = 0) para
 * ejercitar el rechazo por stock insuficiente (RN-06).
 *
 * Se ejecuta explícitamente desde KataecommerceApplication.main una vez que
 * el contexto de Spring está inicializado (no se registra como
 * CommandLineRunner/ApplicationRunner para evitar que se dispare dos veces),
 * siguiendo el mismo patrón que DiscountPolicySeeder.
 *
 * Nota: como Mongo se levanta embebido (de.flapdoodle.mongodb.embedded.version
 * en application.properties), los datos no sobreviven a un reinicio, por lo
 * que este seed se ejecuta en cada arranque y es idempotente (solo siembra si
 * la colección está vacía).
 */
@Component
public class ProductSeeder {

    private static final Logger log = LoggerFactory.getLogger(ProductSeeder.class);

    private final ProductRepository productRepository;

    public ProductSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void seed() {
        if (productRepository.count() > 0) {
            log.info("Ya existen productos; se omite el seed de catálogo.");
            return;
        }

        List<ProductDocument> products = List.of(
                new ProductDocument(UUID.randomUUID().toString(), "Laptop Ultradelgada 14\"",
                        "Portátil liviano con procesador de última generación, ideal para trabajo y estudio.",
                        new BigDecimal("899.99"), ProductCategory.TECNOLOGIA, 12),
                new ProductDocument(UUID.randomUUID().toString(), "Smartphone Gama Media 128GB",
                        "Teléfono con cámara triple y batería de larga duración.",
                        new BigDecimal("349.50"), ProductCategory.TECNOLOGIA, 25),
                new ProductDocument(UUID.randomUUID().toString(), "Auriculares Inalámbricos con Cancelación de Ruido",
                        "Auriculares Bluetooth con cancelación activa de ruido y estuche de carga.",
                        new BigDecimal("129.00"), ProductCategory.TECNOLOGIA, 40),
                new ProductDocument(UUID.randomUUID().toString(), "Monitor Curvo 27\" 144Hz",
                        "Monitor gaming curvo con alta tasa de refresco y respuesta rápida.",
                        new BigDecimal("279.90"), ProductCategory.TECNOLOGIA, 8),
                new ProductDocument(UUID.randomUUID().toString(), "Teclado Mecánico Retroiluminado",
                        "Teclado mecánico con switches intercambiables e iluminación RGB.",
                        new BigDecimal("59.99"), ProductCategory.TECNOLOGIA, 30),
                new ProductDocument(UUID.randomUUID().toString(), "Mouse Ergonómico Inalámbrico",
                        "Mouse inalámbrico de bajo consumo con sensor de alta precisión.",
                        new BigDecimal("24.50"), ProductCategory.TECNOLOGIA, 50),
                new ProductDocument(UUID.randomUUID().toString(), "Tablet 10\" 64GB",
                        "Tablet liviana con pantalla de alta resolución, ideal para consumo de contenido.",
                        new BigDecimal("219.00"), ProductCategory.TECNOLOGIA, 15),
                new ProductDocument(UUID.randomUUID().toString(), "Smartwatch con Monitor de Ritmo Cardíaco",
                        "Reloj inteligente con GPS, monitor de ritmo cardíaco y resistencia al agua.",
                        new BigDecimal("149.99"), ProductCategory.TECNOLOGIA, 20),
                new ProductDocument(UUID.randomUUID().toString(), "Disco SSD Externo 1TB",
                        "Almacenamiento externo de alta velocidad por conexión USB-C.",
                        new BigDecimal("89.90"), ProductCategory.TECNOLOGIA, 18),
                new ProductDocument(UUID.randomUUID().toString(), "Consola de Videojuegos de Última Generación",
                        "Consola de sobremesa con soporte 4K, alta demanda y stock agotado.",
                        new BigDecimal("499.99"), ProductCategory.TECNOLOGIA, 0),
                new ProductDocument(UUID.randomUUID().toString(), "Cámara de Seguridad Wi-Fi",
                        "Cámara IP para interiores con visión nocturna, actualmente sin unidades disponibles.",
                        new BigDecimal("39.99"), ProductCategory.TECNOLOGIA, 0),
                new ProductDocument(UUID.randomUUID().toString(), "Mochila para Portátil Antirrobo",
                        "Mochila resistente al agua con compartimento acolchado para portátil.",
                        new BigDecimal("45.00"), ProductCategory.OTRO, 22),
                new ProductDocument(UUID.randomUUID().toString(), "Silla Ergonómica de Oficina",
                        "Silla ajustable con soporte lumbar para largas jornadas de trabajo.",
                        new BigDecimal("189.00"), ProductCategory.OTRO, 6)
        );

        productRepository.saveAll(products);
        log.info("Catálogo sembrado con {} productos.", products.size());
    }
}
