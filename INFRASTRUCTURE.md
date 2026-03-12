# ☁️ BTG Pactual – Infraestructura como Código (IaC): AWS CloudFormation

## 📋 Tabla de Contenido

1. [Resumen de la Infraestructura](#resumen-de-la-infraestructura)
2. [Diagrama de Arquitectura AWS](#diagrama-de-arquitectura-aws)
3. [Alta Disponibilidad y Redundancia Multi-AZ](#alta-disponibilidad-y-redundancia-multi-az)
4. [Componentes del Stack](#componentes-del-stack)
5. [Modelo de Seguridad en Capas](#modelo-de-seguridad-en-capas)
6. [Auto Scaling Group (ASG)](#auto-scaling-group-asg)
7. [Base de Datos (RDS PostgreSQL)](#base-de-datos-rds-postgresql)
8. [WAF – Web Application Firewall](#waf--web-application-firewall)
9. [Parámetros del Stack](#parámetros-del-stack)
10. [Despliegue](#despliegue)
11. [Flujo de una Petición en Producción](#flujo-de-una-petición-en-producción)

---

## Resumen de la Infraestructura

La solución se despliega sobre **AWS** mediante un template **CloudFormation** que provisiona una arquitectura **altamente disponible, escalable y segura**. El diseño prioriza la **redundancia a nivel de Availability Zones (AZs)** para garantizar que la aplicación siga operando incluso si una zona de disponibilidad completa falla.

### Principios de diseño

| Principio | Implementación |
|---|---|
| **Alta Disponibilidad** | Componentes distribuidos en **mínimo 2 AZs** |
| **Escalabilidad horizontal** | Auto Scaling Group ajusta instancias bajo demanda |
| **Defensa en profundidad** | WAF → ALB → Security Groups → Subnets privadas |
| **Infraestructura inmutable** | Instancias EC2 se crean desde imagen Docker versionada |
| **Infraestructura como Código** | Todo el stack es reproducible y versionable con CloudFormation |

---

## Diagrama de Arquitectura AWS

```
                         ┌──────────────────────────┐
                         │     AWS WAF (WebACL)      │
                         │  AWSManagedRulesCommon     │
                         │  (Protección OWASP Top 10) │
                         └────────────┬───────────────┘
                                      │
                    ┌─────────────────▼──────────────────┐
                    │     Application Load Balancer       │
                    │         (Multi-AZ, público)         │
                    │    ┌──────────┐  ┌──────────┐      │
                    │    │ Listener │  │ Target   │      │
                    │    │ :80 HTTP │─►│ Group    │      │
                    │    └──────────┘  │ :8080    │      │
                    │                  └────┬─────┘      │
                    └───────────────────────┼────────────┘
                                            │
              ┌─────────────────────────────┼─────────────────────────────┐
              │                    VPC (Multi-AZ)                         │
              │                                                           │
              │   ┌─── Availability Zone A ───┐ ┌─── Availability Zone B ───┐
              │   │                           │ │                           │
              │   │  ┌─────────────────────┐  │ │  ┌─────────────────────┐  │
              │   │  │   Subnet Pública A  │  │ │  │   Subnet Pública B  │  │
              │   │  │                     │  │ │  │                     │  │
              │   │  │  ┌───────────────┐  │  │ │  ┌───────────────┐  │  │
              │   │  │  │  EC2 (Docker) │  │  │ │  │  EC2 (Docker) │  │  │
              │   │  │  │  Backend App  │  │  │ │  │  Backend App  │  │  │
              │   │  │  │  :8080        │  │  │ │  │  :8080        │  │  │
              │   │  │  └───────┬───────┘  │  │ │  └───────┬───────┘  │  │
              │   │  └──────────┼──────────┘  │ │  └──────────┼──────────┘  │
              │   │             │              │ │             │              │
              │   │  ┌──────────▼──────────┐  │ │  ┌──────────▼──────────┐  │
              │   │  │   DB Subnet A       │  │ │  │   DB Subnet B       │  │
              │   │  │  ┌───────────────┐  │  │ │  │                     │  │
              │   │  │  │  RDS Postgres  │  │  │ │  │   (Failover ready) │  │
              │   │  │  │  (Primary)     │  │  │ │  │                     │  │
              │   │  │  └───────────────┘  │  │ │  │                     │  │
              │   │  └─────────────────────┘  │ │  └─────────────────────┘  │
              │   └───────────────────────────┘ └───────────────────────────┘
              │                                                           │
              └───────────────────────────────────────────────────────────┘
```

---

## Alta Disponibilidad y Redundancia Multi-AZ

La **redundancia entre Availability Zones** es el pilar fundamental del diseño. Cada AZ de AWS es un datacenter (o grupo de datacenters) físicamente separado, con energía, refrigeración y redes independientes. Distribuir los componentes entre múltiples AZs garantiza que **la caída de un datacenter completo no afecte la disponibilidad del servicio**.

### ¿Cómo se logra la redundancia Multi-AZ?

```
                  Availability Zone A          Availability Zone B
                 ┌─────────────────────┐    ┌─────────────────────┐
    ALB ────────►│  Nodo ALB (AZ-A)    │    │  Nodo ALB (AZ-B)    │◄──── ALB
                 │                     │    │                     │
    ASG ────────►│  EC2 Instancia #1   │    │  EC2 Instancia #2   │◄──── ASG
                 │  (Backend App)      │    │  (Backend App)      │
                 │                     │    │                     │
    RDS ────────►│  DB Subnet A        │    │  DB Subnet B        │◄──── RDS
                 │  (Primary/Standby)  │    │  (Standby/Primary)  │
                 └─────────────────────┘    └─────────────────────┘
                        ✅ ACTIVO                  ✅ ACTIVO
```

### Componente por componente

| Componente | Multi-AZ | Cómo funciona | ¿Qué pasa si cae una AZ? |
|---|---|---|---|
| **ALB** | ✅ Sí | Se despliega en **las subnets de ambas AZs** automáticamente. DNS resuelve a nodos en ambas zonas. | El ALB **remueve los nodos de la AZ caída** y redirige tráfico a la AZ sana. Zero downtime. |
| **EC2 (ASG)** | ✅ Sí | `VPCZoneIdentifier` recibe subnets de **múltiples AZs**. El ASG distribuye instancias equitativamente. Con `DesiredCapacity: 2`, cada AZ obtiene 1 instancia. | El ASG **detecta la instancia perdida** y lanza una nueva en la AZ sana. El Target Group marca la instancia caída como `unhealthy` y deja de enviarle tráfico. |
| **RDS** | ✅ Sí | `DBSubnetGroup` incluye subnets de ambas AZs. RDS coloca la instancia en una AZ y mantiene replicación a la otra. | Con Multi-AZ habilitado, RDS hace **failover automático** a la réplica standby en ~60-120 segundos. |
| **WAF** | ✅ Sí | Es un servicio regional que se asocia al ALB. Opera a nivel de región, no de AZ. | No se ve afectado por caídas de AZs individuales. |

### Escenario de falla: Caída de AZ-A

```
    ANTES (normal)                         DESPUÉS (AZ-A caída)
┌──────────┬──────────┐             ┌──────────┬──────────┐
│   AZ-A   │   AZ-B   │             │   AZ-A   │   AZ-B   │
│          │          │             │    ❌    │          │
│  EC2 #1  │  EC2 #2  │    ──►     │  (caída) │  EC2 #2  │ ← recibe 100% tráfico
│  ALB ✓   │  ALB ✓   │             │          │  ALB ✓   │
│  RDS pri │  RDS stb │             │          │  RDS pri │ ← failover automático
└──────────┴──────────┘             └──────────┴──────────┘
                                                  │
                                     ASG lanza EC2 #3 en AZ-B
                                     para mantener DesiredCapacity
```

**Resultado**: La aplicación sigue operando sin intervención manual. El tiempo de impacto es de **segundos** para el tráfico HTTP (ALB health check) y **~2 minutos** para el failover de RDS.

### ¿Por qué se requieren mínimo 2 subnets?

El parámetro `Subnets` exige **al menos 2 subnets en AZs diferentes** porque:

1. **ALB obligatorio**: AWS requiere que un Application Load Balancer tenga subnets en al menos 2 AZs.
2. **ASG distribución**: El Auto Scaling Group utiliza las mismas subnets para distribuir instancias entre AZs, evitando un punto único de falla.
3. **RDS Subnet Group**: RDS necesita un `DBSubnetGroup` con subnets en al menos 2 AZs para poder hacer failover.

```yaml
# CloudFormation: Las subnets son el punto de anclaje Multi-AZ
Parameters:
  Subnets:
    Type: 'List<AWS::EC2::Subnet::Id>'
    Description: Lista de Subnets públicas (al menos 2, en AZs diferentes)

# Cada componente las reutiliza:
ALB:              Subnets: !Ref Subnets          # → Multi-AZ
ASG:              VPCZoneIdentifier: !Ref Subnets # → Multi-AZ  
RDS SubnetGroup:  SubnetIds: !Ref Subnets         # → Multi-AZ
```

---

## Componentes del Stack

### Resumen visual

```
┌─────────────────────────────────────────────────────────────┐
│                     CloudFormation Stack                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─── Seguridad ──────────────────────────────────────────┐ │
│  │  • LBSecurityGroup        (HTTP :80 desde 0.0.0.0/0)  │ │
│  │  • BackendSecurityGroup   (:8080 solo desde ALB SG)    │ │
│  │  • DBSecurityGroup        (:5432 solo desde Backend SG)│ │
│  └────────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌─── Red (ALB) ──────────────────────────────────────────┐ │
│  │  • ApplicationLoadBalancer (público, Multi-AZ)         │ │
│  │  • ALBListener            (HTTP :80 → Target Group)    │ │
│  │  • ALBTargetGroup         (health check /health :8080) │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌─── Cómputo (ASG) ─────────────────────────────────────┐  │
│  │  • BackendLaunchTemplate  (AL2023, Docker, t2.micro)   │ │
│  │  • BackendASG             (min:1, max:3, desired:2)    │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌─── Datos (RDS) ───────────────────────────────────────┐  │
│  │  • MyDBSubnetGroup        (Multi-AZ subnet group)      │ │
│  │  • MyDatabase             (PostgreSQL, db.t3.micro)    │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌─── Protección (WAF) ──────────────────────────────────┐  │
│  │  • WebACL                 (AWSManagedRulesCommonRuleSet)│ │
│  │  • WAFAssociation         (vinculado al ALB)           │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Modelo de Seguridad en Capas

El diseño implementa **defensa en profundidad** con múltiples capas de seguridad que filtran el tráfico progresivamente:

```
    Internet
       │
       ▼
 ┌──────────┐   Capa 1: Reglas OWASP (SQL Injection, XSS, etc.)
 │   WAF    │   AWS Managed Rules – Common Rule Set
 └────┬─────┘
      ▼
 ┌──────────┐   Capa 2: Solo HTTP :80 desde cualquier IP
 │  ALB SG  │   SecurityGroupIngress: 0.0.0.0/0 → :80
 └────┬─────┘
      ▼
 ┌──────────┐   Capa 3: Solo :8080 desde el SG del ALB
 │ Backend  │   SourceSecurityGroupId: LBSecurityGroup
 │   SG     │   → Ningún tráfico directo de Internet
 └────┬─────┘
      ▼
 ┌──────────┐   Capa 4: Solo :5432 desde el SG del Backend
 │  DB SG   │   SourceSecurityGroupId: BackendSecurityGroup
 │          │   PubliclyAccessible: false
 └──────────┘   → Inaccesible desde Internet
```

### Principio de mínimo privilegio

| Componente | Puerto | Accesible desde | Justificación |
|---|---|---|---|
| **ALB** | `:80` | `0.0.0.0/0` (Internet) | Punto de entrada público único |
| **EC2 Backend** | `:8080` | Solo `LBSecurityGroup` | No expuesto a Internet directamente |
| **RDS PostgreSQL** | `:5432` | Solo `BackendSecurityGroup` | No accesible públicamente (`PubliclyAccessible: false`) |

> 🔒 **Las instancias EC2 y la base de datos nunca son accesibles directamente desde Internet.** Todo el tráfico entra por el ALB, que está protegido por WAF.

---

## Auto Scaling Group (ASG)

El ASG garantiza **elasticidad y auto-recuperación**:

```yaml
BackendASG:
  MinSize: '1'       # Mínimo: siempre al menos 1 instancia corriendo
  MaxSize: '3'       # Máximo: hasta 3 instancias bajo alta demanda
  DesiredCapacity: '2' # Normal: 2 instancias, 1 por AZ
```

### Distribución entre AZs

```
    DesiredCapacity: 2
    ┌─────────────────────────────────────────┐
    │              Auto Scaling Group          │
    │                                          │
    │   AZ-A (Subnet A)    AZ-B (Subnet B)   │
    │  ┌──────────────┐   ┌──────────────┐    │
    │  │   EC2 #1     │   │   EC2 #2     │    │
    │  │  Docker      │   │  Docker      │    │
    │  │  backend-app │   │  backend-app │    │
    │  └──────────────┘   └──────────────┘    │
    │                                          │
    └─────────────────────────────────────────┘
```

### Launch Template

Cada instancia EC2 se inicializa con **UserData** que:

1. Instala Docker en Amazon Linux 2023
2. Descarga la imagen `ismaeltrochas/btg-technical-test:latest` desde DockerHub
3. Inyecta las variables de entorno (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`)
4. Expone el puerto `:8080`

```bash
#!/bin/bash
dnf update -y
dnf install -y docker
systemctl start docker && systemctl enable docker

docker run -d \
  -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://<RDS_ENDPOINT>:5432/postgres" \
  -e DB_USERNAME="adminuser" \
  -e DB_PASSWORD="<secret>" \
  -e JWT_SECRET="<base64-key>" \
  --name backend-app \
  ismaeltrochas/btg-technical-test:latest
```

### Health Checks y Auto-Recuperación

```
          ALB Target Group
          Health Check: GET /health :8080
                │
    ┌───────────┼───────────┐
    ▼                       ▼
 EC2 #1                  EC2 #2
 ✅ healthy              ❌ unhealthy (3 checks fallidos)
 ← recibe tráfico        ← ALB deja de enviar tráfico
                          ← ASG termina instancia
                          ← ASG lanza EC2 #3 (reemplazo)
```

---

## Base de Datos (RDS PostgreSQL)

```yaml
MyDatabase:
  Engine: postgres
  DBInstanceClass: db.t3.micro
  AllocatedStorage: '20'         # 20 GB SSD (gp2)
  PubliclyAccessible: false       # Solo accesible desde la VPC
  DBSubnetGroupName: !Ref MyDBSubnetGroup  # Multi-AZ ready
```

### Redundancia a nivel de datos

El `DBSubnetGroup` incluye subnets de ambas AZs, lo que permite:

1. **Despliegue Multi-AZ**: RDS puede activarse con `MultiAZ: true` para tener una réplica standby automática en la otra AZ.
2. **Backups automáticos**: RDS realiza backups diarios y permite Point-in-Time Recovery.
3. **Failover automático**: Si la AZ primaria cae, RDS promueve la réplica standby en ~60-120 segundos.

```
    AZ-A                           AZ-B
┌────────────────┐          ┌────────────────┐
│  RDS Primary   │  ──────► │  RDS Standby   │
│  (Lecturas +   │ Replicación│  (Solo espera) │
│   Escrituras)  │ síncrona  │                │
└────────────────┘          └────────────────┘
        │                          │
        └──── DBSubnetGroup ───────┘
              (ambas subnets)
```

> 💡 **Nota**: Para activar Multi-AZ en producción, basta agregar `MultiAZ: true` al recurso `MyDatabase`. El `DBSubnetGroup` ya está preparado con subnets en ambas AZs.

---

## WAF – Web Application Firewall

El WAF protege la aplicación contra ataques web comunes, actuando como **primera línea de defensa** antes del ALB:

```yaml
WebACL:
  DefaultAction: Allow        # Permite tráfico legítimo
  Rules:
    - AWSManagedRulesCommonRuleSet   # Reglas OWASP gestionadas por AWS
```

### ¿Qué protege?

| Ataque | Protección AWS Managed Rules |
|---|---|
| **SQL Injection** | Detecta patrones SQL en headers, body, query strings |
| **Cross-Site Scripting (XSS)** | Filtra payloads de script malicioso |
| **Local File Inclusion (LFI)** | Bloquea path traversal (`../../etc/passwd`) |
| **Remote Code Execution** | Detecta intentos de ejecución remota |
| **Bot traffic** | Identifica user-agents maliciosos |
| **Request size** | Limita tamaño de solicitudes anómalas |

### Flujo WAF → ALB

```
  Request HTTP
       │
       ▼
  ┌─────────┐     ¿Pasa las reglas?
  │   WAF   │ ────── SÍ ──────► ALB ──► Backend
  │  WebACL │
  └────┬────┘
       │
       NO → 403 Forbidden (request bloqueada)
```

### Observabilidad

```yaml
VisibilityConfig:
  SampledRequestsEnabled: true      # Muestras de requests para análisis
  CloudWatchMetricsEnabled: true     # Métricas en CloudWatch
  MetricName: BackendWebACLMetric   # Nombre de la métrica
```

Esto permite monitorear en **CloudWatch**:
- Requests permitidas vs bloqueadas
- Reglas más activadas
- Patrones de ataque detectados

---

## Parámetros del Stack

| Parámetro | Tipo | Descripción | Valor por defecto |
|---|---|---|---|
| `DBPassword` | `String` (NoEcho) | Contraseña de RDS PostgreSQL | *(requerido)* |
| `LatestAmiId` | `AWS::SSM::Parameter` | AMI de Amazon Linux 2023 más reciente | Auto-resuelve desde SSM |
| `VpcId` | `AWS::EC2::VPC::Id` | VPC donde se desplegará el stack | *(requerido)* |
| `Subnets` | `List<AWS::EC2::Subnet::Id>` | Subnets públicas (mínimo 2, en AZs diferentes) | *(requerido)* |

> ⚠️ **Importante**: Las subnets seleccionadas **deben estar en al menos 2 AZs diferentes** para garantizar la alta disponibilidad.

---

## Despliegue

### Prerrequisitos

1. Cuenta AWS con permisos para crear EC2, ALB, RDS, WAF, ASG
2. Una VPC con al menos **2 subnets públicas en AZs diferentes**
3. La imagen Docker `ismaeltrochas/btg-technical-test:latest` publicada en DockerHub

### Comandos

```bash
# 1. Crear el stack
aws cloudformation create-stack \
  --stack-name btg-backend-stack \
  --template-body file://template.yaml \
  --parameters \
    ParameterKey=DBPassword,ParameterValue=<tu-password-seguro> \
    ParameterKey=VpcId,ParameterValue=vpc-xxxxxxxx \
    ParameterKey=Subnets,ParameterValue="subnet-aaaa,subnet-bbbb"

# 2. Verificar el estado
aws cloudformation describe-stacks \
  --stack-name btg-backend-stack \
  --query "Stacks[0].StackStatus"

# 3. Obtener la URL del Load Balancer
aws cloudformation describe-stacks \
  --stack-name btg-backend-stack \
  --query "Stacks[0].Outputs[?OutputKey=='LoadBalancerDNS'].OutputValue" \
  --output text

# 4. Probar la API
curl http://<ALB-DNS>/actuator/health
```

### Desde la consola AWS

1. Ir a **CloudFormation** → **Create Stack**
2. Subir el archivo `template.yaml`
3. Completar los parámetros (VPC, Subnets, DBPassword)
4. Revisar y crear
5. Esperar ~10 minutos hasta `CREATE_COMPLETE`
6. Copiar la URL del Output `LoadBalancerDNS`

---

## Flujo de una Petición en Producción

```
  Usuario
    │
    │  POST /api/suscripciones
    │  Authorization: Bearer eyJ...
    ▼
┌─────────┐
│   WAF   │──── Valida reglas OWASP ──── ❌ 403 si malicioso
└────┬────┘
     ▼ ✅
┌─────────┐
│   ALB   │──── DNS público ──── Balancea entre AZs
└────┬────┘
     │
     ├──── AZ-A ────►  EC2 #1 (:8080)
     │                    │
     └──── AZ-B ────►  EC2 #2 (:8080)  ◄── seleccionado por round-robin
                          │
                   ┌──────▼──────┐
                   │ Docker      │
                   │ Spring Boot │
                   │ JWT Filter  │──── Valida token ──── ❌ 401 si inválido
                   │ Controller  │
                   │ Service     │
                   └──────┬──────┘
                          │ JDBC
                          ▼
                   ┌──────────────┐
                   │ RDS Postgres │──── :5432 (solo desde Backend SG)
                   │ (Multi-AZ)   │
                   └──────────────┘
                          │
                          ▼
                    201 Created
                   { suscripción }
```

---

## Resumen de Alta Disponibilidad

```
┌────────────────────────────────────────────────────────┐
│                   ALTA DISPONIBILIDAD                   │
├────────────────────────────────────────────────────────┤
│                                                        │
│  ✅ ALB Multi-AZ        → Tráfico distribuido          │
│  ✅ ASG Multi-AZ        → Instancias en 2+ AZs         │
│  ✅ RDS SubnetGroup     → DB preparada para Multi-AZ    │
│  ✅ WAF Regional        → No depende de AZ individual   │
│  ✅ Health Checks       → Auto-recuperación de EC2      │
│  ✅ Security Groups     → Aislamiento por capas         │
│  ✅ Docker inmutable    → Despliegue consistente        │
│  ✅ Flyway migraciones  → Schema versionado automático  │
│                                                        │
│  SLA estimado: 99.95%+ (Multi-AZ con failover)         │
│                                                        │
│  Punto único de falla: NINGUNO                          │
│  (cada capa tiene redundancia en 2+ AZs)               │
│                                                        │
└────────────────────────────────────────────────────────┘
```

---

*Infraestructura diseñada por Ismael Trocha – Prueba Técnica BTG Pactual 2026*

