📄 DOCUMENTO 1
Nome do arquivo sugerido: PLANO_IMPLEMENTACAO_FOTO_COMPROVANTE.md

markdown


# 📋 Plano de Implementação - Funcionalidade de Foto do Comprovante

**Projeto:** Meu Ponto  
**Autor:** Thiago Macedo  
**Data:** 09/03/2026  
**Versão:** 1.0

---

## 🎯 Visão Geral

Este documento detalha os passos necessários para finalizar a implementação da funcionalidade de **captura e armazenamento de foto do comprovante durante o registro de ponto** no aplicativo "Meu Ponto".

---

## 📊 Status Atual da Implementação

### ✅ Já Implementado (Completo)

| Componente | Arquivo | Descrição |
|------------|---------|-----------|
| Domain Model | `Ponto.kt` | Campo `fotoComprovantePath: String?` |
| Room Entity | `PontoEntity.kt` | Campo `fotoComprovantePath` + mappers |
| Config Entity | `ConfiguracaoEmpregoEntity.kt` | Campo `fotoObrigatoria: Boolean` |
| Config Domain | `ConfiguracaoEmprego.kt` | Campo `fotoObrigatoria` mapeado |
| UI State | `HomeUiState.kt` | `fotoComprovanteUri`, `fotoHabilitada`, `fotoObrigatoria`, `fotoValidaParaRegistro` |
| Actions | `HomeAction.kt` | `SelecionarFotoComprovante`, `RemoverFotoComprovante` |
| ViewModel | `HomeViewModel.kt` | `selecionarFotoComprovante()`, `removerFotoComprovante()`, `salvarFotoComprovante()` |
| UI Component | `ComprovanteImagePicker.kt` | Componente completo com câmera e galeria |

### ⚠️ Parcialmente Implementado

| Componente | Arquivo | Problema |
|------------|---------|----------|
| Tela Principal | `HomeScreen.kt` | Código do `ComprovanteImagePicker` está **COMENTADO** (linhas 164-171) |
| Câmera | `ComprovanteImagePicker.kt` | `onCameraUriCreated` retorna `null` no modo simplificado |

### ❌ A Verificar/Implementar

| Componente | Status | Descrição |
|------------|--------|-----------|
| `PontoRepository` | ❓ | Verificar método `atualizarFotoComprovante()` |
| `PontoDao` | ❓ | Verificar query de update |
| Tela de Configurações | ❓ | Toggle para `fotoObrigatoria` |
| Migração de banco | ❓ | Migration para novos campos |
| Tela de Edição | ❓ | Visualizar/editar foto em ponto existente |
| Histórico | ❓ | Indicador de foto nos cards |

---

## 🔧 Passos para Finalização

### FASE 1: Ativar o Componente na HomeScreen

**Prioridade:** 🔴 ALTA  
**Estimativa:** 15-30 minutos

#### Passo 1.1: Descomentar o ComprovanteImagePicker

**Arquivo:** `app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/home/HomeScreen.kt`

**Localização:** Aproximadamente linhas 164-171

**ANTES (comentado):**
```kotlin
//            // ═══════════════════════════════════════════════════════════════
//            // Seletor de foto de comprovante (quando habilitado)
//            // ═══════════════════════════════════════════════════════════════
//            if (uiState.fotoHabilitada && (uiState.podeRegistrarPontoAutomatico || uiState.podeRegistrarPontoManual)) {
//                ComprovanteImagePicker(
//                    fotoSelecionadaUri = uiState.fotoComprovanteUri,
//                    isObrigatorio = uiState.fotoObrigatoria,
//                    onFotoSelecionada = { uri -> onAction(HomeAction.SelecionarFotoComprovante(uri)) },
//                    onRemoverFoto = { onAction(HomeAction.RemoverFotoComprovante) }
//                )
//            }
DEPOIS (ativado):

kotlin


// ═══════════════════════════════════════════════════════════════
// Seletor de foto de comprovante (quando habilitado)
// ═══════════════════════════════════════════════════════════════
if (uiState.fotoHabilitada && (uiState.podeRegistrarPontoAutomatico || uiState.podeRegistrarPontoManual)) {
    ComprovanteImagePicker(
        fotoSelecionadaUri = uiState.fotoComprovanteUri,
        isObrigatorio = uiState.fotoObrigatoria,
        onFotoSelecionada = { uri -> onAction(HomeAction.SelecionarFotoComprovante(uri)) },
        onRemoverFoto = { onAction(HomeAction.RemoverFotoComprovante) }
    )
}
Passo 1.2: Habilitar suporte à câmera (Opcional - Melhoria)
O componente simplificado desabilita a câmera. Para habilitar, use a versão completa:

Opção A - Modificar chamada para versão completa:

kotlin


if (uiState.fotoHabilitada && (uiState.podeRegistrarPontoAutomatico || uiState.podeRegistrarPontoManual)) {
    ComprovanteImagePicker(
        currentImagePath = null,
        currentImageUri = uiState.fotoComprovanteUri,
        imageBaseDir = null,
        onImageSelected = { uri -> onAction(HomeAction.SelecionarFotoComprovante(uri)) },
        onImageRemoved = { onAction(HomeAction.RemoverFotoComprovante) },
        onCameraUriCreated = { viewModel.criarUriTemporarioParaCamera() }
    )
}
Opção B - Adicionar função pública no ViewModel:

kotlin


// Em HomeViewModel.kt - tornar função pública
fun criarUriTemporarioParaCamera(): Uri? {
    return criarTempFileUri()
}
FASE 2: Verificar/Implementar Repository
Prioridade: 🔴 ALTA
Estimativa: 30-60 minutos

Passo 2.1: Verificar existência dos métodos
Execute os comandos:

bash


# Verificar interface
grep -n "atualizarFotoComprovante\|fotoComprovante" \
  app/src/main/java/br/com/tlmacedo/meuponto/domain/repository/PontoRepository.kt

# Verificar implementação
grep -n "atualizarFotoComprovante\|fotoComprovante" \
  app/src/main/java/br/com/tlmacedo/meuponto/data/repository/PontoRepositoryImpl.kt

# Verificar DAO
grep -n "atualizarFotoComprovante\|fotoComprovante" \
  app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/dao/PontoDao.kt
Passo 2.2: Implementar se não existir
Interface (domain/repository/PontoRepository.kt):

kotlin


interface PontoRepository {
    // ... outros métodos existentes
    
    /**
     * Atualiza o caminho da foto de comprovante de um ponto.
     * @param pontoId ID do ponto
     * @param fotoPath Caminho relativo da foto (ou null para remover)
     * @return true se atualizado com sucesso
     */
    suspend fun atualizarFotoComprovante(pontoId: Long, fotoPath: String?): Boolean
}
Implementação (data/repository/PontoRepositoryImpl.kt):

kotlin


override suspend fun atualizarFotoComprovante(pontoId: Long, fotoPath: String?): Boolean {
    return try {
        pontoDao.atualizarFotoComprovante(
            pontoId = pontoId, 
            fotoPath = fotoPath, 
            atualizadoEm = LocalDateTime.now()
        )
        true
    } catch (e: Exception) {
        Log.e("PontoRepository", "Erro ao atualizar foto: ${e.message}")
        false
    }
}
Passo 2.3: Implementar no DAO
Arquivo: data/local/database/dao/PontoDao.kt

kotlin


@Query("""
    UPDATE pontos 
    SET fotoComprovantePath = :fotoPath, 
        atualizadoEm = :atualizadoEm 
    WHERE id = :pontoId
""")
suspend fun atualizarFotoComprovante(
    pontoId: Long, 
    fotoPath: String?, 
    atualizadoEm: LocalDateTime
)
FASE 3: Migração de Banco de Dados
Prioridade: 🔴 ALTA
Estimativa: 30 minutos

Passo 3.1: Verificar versão atual
bash


# Ver versão do banco
cat app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/AppDatabase.kt | grep -A5 "version"

# Listar migrations existentes
ls -la app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/migration/
Passo 3.2: Criar migration (se necessário)
Arquivo: data/local/database/migration/Migration_X_Y.kt

kotlin


package br.com.tlmacedo.meuponto.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration para adicionar suporte a foto de comprovante.
 * - Adiciona coluna fotoComprovantePath na tabela pontos
 * - Adiciona coluna fotoObrigatoria na tabela configuracoes_emprego
 */
val MIGRATION_X_Y = object : Migration(X, Y) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Adicionar coluna em pontos
        database.execSQL(
            """
            ALTER TABLE pontos 
            ADD COLUMN fotoComprovantePath TEXT DEFAULT NULL
            """
        )
        
        // Adicionar coluna em configuracoes_emprego
        database.execSQL(
            """
            ALTER TABLE configuracoes_emprego 
            ADD COLUMN fotoObrigatoria INTEGER NOT NULL DEFAULT 0
            """
        )
    }
}
Passo 3.3: Registrar migration no AppDatabase
kotlin


@Database(
    entities = [...],
    version = Y, // Incrementar versão
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    // ...
}

// No builder do Room (geralmente em DatabaseModule.kt):
Room.databaseBuilder(context, AppDatabase::class.java, "meuponto.db")
    .addMigrations(
        // ... outras migrations
        MIGRATION_X_Y
    )
    .build()
FASE 4: Configurações do Emprego
Prioridade: 🟡 MÉDIA
Estimativa: 45-90 minutos

Passo 4.1: Localizar tela de configuração
bash


# Verificar arquivos de configuração do emprego
ls -la app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/
cat app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/settings/empregos/editar/*.kt
Passo 4.2: Adicionar toggle na UI
kotlin


// Na seção de configurações do emprego, adicionar:

// Seção: Registro de Ponto
SettingsSection(title = "Registro de Ponto") {
    
    // Toggle existente de NSR...
    
    // Toggle existente de Localização...
    
    // NOVO: Toggle de Foto
    SwitchPreference(
        title = "Foto de Comprovante",
        subtitle = if (configuracao.fotoObrigatoria) {
            "Obrigatório anexar foto ao registrar ponto"
        } else {
            "Desativado"
        },
        checked = configuracao.fotoObrigatoria,
        onCheckedChange = { novoValor ->
            onConfiguracaoChange(configuracao.copy(fotoObrigatoria = novoValor))
        },
        icon = Icons.Default.CameraAlt
    )
}
FASE 5: Tela de Edição de Ponto
Prioridade: 🟡 MÉDIA
Estimativa: 60-90 minutos

Passo 5.1: Verificar estrutura atual
bash


cat app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/editponto/EditPontoScreen.kt
cat app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/editponto/EditPontoViewModel.kt
cat app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/editponto/EditPontoUiState.kt
Passo 5.2: Adicionar campos no UiState
kotlin


data class EditPontoUiState(
    // ... campos existentes
    
    // Foto de comprovante
    val fotoComprovantePath: String? = null,  // Caminho salvo no banco
    val fotoComprovanteUri: Uri? = null,       // URI temporário (nova seleção)
    val fotoHabilitada: Boolean = false,
    val fotoAlterada: Boolean = false
)
Passo 5.3: Adicionar componente na tela
kotlin


// Na EditPontoScreen, após os campos de hora/observação:

if (uiState.fotoHabilitada || uiState.fotoComprovantePath != null) {
    Spacer(modifier = Modifier.height(16.dp))
    
    ComprovanteImagePicker(
        currentImagePath = uiState.fotoComprovantePath,
        currentImageUri = uiState.fotoComprovanteUri,
        imageBaseDir = viewModel.getImageBaseDir(),
        onImageSelected = { uri -> 
            viewModel.onAction(EditPontoAction.SelecionarFoto(uri)) 
        },
        onImageRemoved = { 
            viewModel.onAction(EditPontoAction.RemoverFoto) 
        },
        onCameraUriCreated = { viewModel.criarUriParaCamera() }
    )
}
FASE 6: Indicadores Visuais
Prioridade: 🟢 BAIXA
Estimativa: 30-45 minutos

Passo 6.1: Adicionar ícone no IntervaloCard
Arquivo: presentation/components/IntervaloCard.kt (ou similar)

kotlin


@Composable
fun PontoItem(
    ponto: Ponto,
    // ... outros parâmetros
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Hora
        Text(
            text = ponto.horaFormatada,
            style = MaterialTheme.typography.titleMedium
        )
        
        // Indicadores
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            // NSR
            if (ponto.temNsr) {
                Icon(
                    imageVector = Icons.Default.Numbers,
                    contentDescription = "NSR: ${ponto.nsr}",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Localização
            if (ponto.temLocalizacao) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Localização registrada",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // NOVO: Foto
            if (ponto.temFotoComprovante) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Foto anexada",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
Passo 6.2: Preview da foto ao tocar
kotlin


var showFotoPreview by remember { mutableStateOf(false) }

// Tornar ícone clicável
if (ponto.temFotoComprovante) {
    Icon(
        imageVector = Icons.Default.Image,
        contentDescription = "Ver foto",
        modifier = Modifier
            .size(16.dp)
            .clickable { showFotoPreview = true },
        tint = MaterialTheme.colorScheme.primary
    )
}

// Dialog de preview
if (showFotoPreview && ponto.fotoComprovantePath != null) {
    ImagePreviewDialog(
        imagePath = File(imageBaseDir, ponto.fotoComprovantePath),
        onDismiss = { showFotoPreview = false }
    )
}
📁 Estrutura de Armazenamento de Fotos
Diretório Recomendado


/data/data/br.com.tlmacedo.meuponto/files/
└── comprovantes/
    └── {empregoId}/
        └── {ano}/
            └── {mes}/
                └── ponto_{pontoId}_{timestamp}.jpg
Padrão de Nome do Arquivo


ponto_123_20260309_143022.jpg
      │     │        │
      │     │        └── HHmmss (hora do registro)
      │     └── yyyyMMdd (data do registro)
      └── ID do ponto
Caminho Relativo Salvo no Banco


1/2026/03/ponto_123_20260309_143022.jpg
│  │   │
│  │   └── Mês
│  └── Ano
└── ID do emprego
⚠️ Possíveis Dificuldades e Mitigações



Dificuldade	Impacto	Mitigação
Permissão de câmera negada	Médio	Mensagem clara ao usuário + fallback para galeria
Arquivo muito grande	Alto	Comprimir imagem antes de salvar (max 1MB, 80% quality)
Migration em app instalado	Alto*	Testar migration com dados existentes antes de release
URI temporário expira	Médio	Salvar arquivo permanente imediatamente após captura
Armazenamento cheio	Médio	Verificar espaço antes de salvar + mensagem de erro clara
Foto órfã após exclusão*	Baixo	Implementar cleanup ao excluir ponto
Rotação de imagem	Baixo	Ler EXIF e corrigir orientação
✅ Checklist de Testes
Testes Funcionais
 Registrar ponto COM foto obrigatória habilitada
 Sem foto: deve bloquear e exibir mensagem
 Com foto: deve registrar normalmente
 Registrar ponto SEM foto obrigatória
 Sem foto: deve registrar normalmente
 Com foto: deve registrar com foto
 Capturar foto com câmera
 Selecionar foto da galeria
 Remover foto antes de registrar
 Cancelar seleção de foto
 Visualizar foto em ponto existente (toque no ícone)
 Editar ponto e alterar foto
 Editar ponto e remover foto
 Excluir ponto com foto (arquivo deve ser removido)
Testes de Persistência
 Registrar ponto com foto → fechar app → reabrir → foto deve estar lá
 Migration: instalar versão antiga → popular dados → atualizar → verificar dados intactos
Testes de Borda
 Tentar salvar foto com armazenamento cheio
 Negar permissão de câmera → deve oferecer galeria
 Foto muito grande (>5MB) → deve comprimir
 Múltiplos pontos com foto no mesmo dia
⏱️ Estimativa de Tempo



Fase	Descrição	Estimativa
1	Ativar componente na HomeScreen	15-30 min
2	Verificar/implementar Repository e DAO	30-60 min
3*	Migração de banco de dados	30 min
4	Toggle nas configurações do emprego	45-90 min
5	Tela de edição de ponto	60-90 min
6	Indicadores visuais	30-45 min
-	Testes e ajustes	60 min
TOTAL		4.5 - 7 horas
📝 Comandos Úteis
bash


# Verificar Repository
cat app/src/main/java/br/com/tlmacedo/meuponto/domain/repository/PontoRepository.kt
cat app/src/main/java/br/com/tlmacedo/meuponto/data/repository/PontoRepositoryImpl.kt

# Verificar DAO
cat app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/dao/PontoDao.kt

# Verificar banco e migrations
cat app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/AppDatabase.kt
ls -la app/src/main/java/br/com/tlmacedo/meuponto/data/local/database/migration/

# Verificar tela de edição
ls -la app/src/main/java/br/com/tlmacedo/meuponto/presentation/screen/editponto/

# Verificar usecases de foto existentes
ls -la app/src/main/java/br/com/tlmacedo/meuponto/domain/usecase/foto/
🎯 Próximo Passo Imediato
Ao retornar, execute:

Descomentar o código na HomeScreen.kt (linhas ~164-171)
Testar se o componente aparece quando fotoObrigatoria = true
Verificar console para erros de compilação ou runtime
Seguir para as próximas fases conforme necessidade