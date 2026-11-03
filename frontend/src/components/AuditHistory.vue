<template>
  <div class="space-y-4">
    <!-- 顶部状态指引卡片 -->
    <div class="bg-gradient-to-r from-stone-900 to-stone-800 text-white p-4 md:p-5 rounded-2xl shadow-md border border-stone-700 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
      <div>
        <div class="flex items-center space-x-2">
          <span class="text-lg">📜</span>
          <h2 class="text-base font-bold text-emerald-400">处方安全审计流水与存证追溯</h2>
          <span class="text-[10px] px-2 py-0.5 rounded-full bg-emerald-950 text-emerald-300 border border-emerald-700/60 font-mono">
            医疗合规不可篡改
          </span>
        </div>
        <p class="text-xs text-stone-300 mt-1 leading-relaxed">
          所有经由 RapidOCR 识别或药师录入的处方审查结论均自动入库留痕，记录精确至毫秒，支持处方防伪追溯与责任倒查。
        </p>
      </div>

      <div class="flex items-center space-x-2 self-end sm:self-center">
        <button 
          @click="fetchHistory" 
          :disabled="isLoading"
          class="text-xs bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white px-3 py-1.5 rounded-lg transition-colors flex items-center space-x-1 shadow-sm"
        >
          <span :class="{ 'animate-spin': isLoading }">🔄</span>
          <span>{{ isLoading ? '刷新中...' : '刷新流水' }}</span>
        </button>
      </div>
    </div>

    <!-- 筛选条件栏与统计 -->
    <div class="flex flex-wrap items-center justify-between gap-2 bg-white p-3 rounded-xl border border-stone-200 text-xs shadow-sm">
      <div class="flex items-center space-x-1 overflow-x-auto">
        <button 
          v-for="f in filterOptions" 
          :key="f.key"
          @click="currentFilter = f.key"
          class="px-3 py-1 rounded-lg transition-all font-medium whitespace-nowrap"
          :class="currentFilter === f.key ? 'bg-stone-900 text-white shadow-sm' : 'bg-stone-100 text-stone-600 hover:bg-stone-200'"
        >
          {{ f.label }}
          <span class="ml-1 text-[10px] opacity-75">({{ getCountByStatus(f.key) }})</span>
        </button>
      </div>

      <div class="text-[11px] text-stone-500">
        已加载 <strong class="text-stone-800">{{ filteredRecords.length }}</strong> 条存证记录
      </div>
    </div>

    <!-- 加载中动画 -->
    <div v-if="isLoading && records.length === 0" class="py-12 flex flex-col items-center justify-center space-y-2 text-stone-400 text-xs">
      <div class="w-7 h-7 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin"></div>
      <span>正在从持久化数据库同步审计存证记录...</span>
    </div>

    <!-- 空数据展示 -->
    <div v-else-if="filteredRecords.length === 0" class="py-12 bg-white rounded-2xl border border-dashed border-stone-300 text-center space-y-3">
      <div class="text-4xl text-stone-300">📋</div>
      <div class="text-xs text-stone-600 font-medium">当前分类下暂无处方质检留痕记录</div>
      <p class="text-[11px] text-stone-400">前往“处方配伍质检”页面提交处方，系统将自动入库生成存证记录。</p>
    </div>

    <!-- 流水列表展示 -->
    <div v-else class="space-y-3">
      <div 
        v-for="record in filteredRecords" 
        :key="record.id"
        class="bg-white p-4 rounded-xl border transition-all hover:shadow-md space-y-3"
        :class="getCardBorderClass(record.safetyStatus)"
      >
        <!-- 头部流水号与时间 -->
        <div class="flex flex-wrap items-center justify-between gap-2 pb-2.5 border-b border-stone-100">
          <div class="flex items-center space-x-2">
            <span class="font-mono text-xs font-bold text-stone-800 bg-stone-100 px-2 py-0.5 rounded border border-stone-200">
              {{ record.auditSerialNo }}
            </span>
            <span 
              class="text-[11px] px-2 py-0.5 rounded-full font-bold flex items-center space-x-1"
              :class="getStatusBadgeClass(record.safetyStatus)"
            >
              <span>{{ getStatusIcon(record.safetyStatus) }}</span>
              <span>{{ getStatusText(record.safetyStatus) }}</span>
            </span>
          </div>

          <div class="text-[11px] text-stone-400 font-mono flex items-center space-x-2">
            <span>🕒 {{ formatTime(record.createdAt) }}</span>
          </div>
        </div>

        <!-- 药材药味构成 -->
        <div>
          <div class="text-[11px] text-stone-400 mb-1.5 flex justify-between">
            <span>处方调配中药药材组成：</span>
            <span>共 {{ parseHerbs(record.detectedHerbs).length }} 味</span>
          </div>
          <div class="flex flex-wrap gap-1.5">
            <span 
              v-for="(herb, hIdx) in parseHerbs(record.detectedHerbs)" 
              :key="hIdx"
              class="px-2 py-0.5 bg-stone-50 border border-stone-200 text-stone-700 rounded text-xs"
            >
              <strong class="text-stone-900">{{ herb.name }}</strong>
              <span class="text-stone-500 ml-1 font-mono text-[11px]">{{ herb.dose }}g</span>
            </span>
          </div>
        </div>

        <!-- 违规详情提示框 (若有) -->
        <div 
          v-if="parseContraindications(record.contraindications).length > 0"
          class="p-2.5 bg-rose-50 rounded-lg border border-rose-200 text-xs text-rose-900 space-y-1.5"
        >
          <div class="font-bold flex items-center text-rose-800">
            <span class="mr-1">🚨</span> 触发法定配伍红线明细：
          </div>
          <div 
            v-for="(warn, wIdx) in parseContraindications(record.contraindications)" 
            :key="wIdx"
            class="text-[11px] text-rose-700 pl-4 border-l-2 border-rose-400 space-y-0.5"
          >
            <div class="font-bold">【{{ warn.type }}】{{ warn.pair ? warn.pair.join(' ❌ ') : '' }}</div>
            <p class="text-[10px] text-stone-600 leading-tight">{{ warn.quote }}</p>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="pt-1 flex items-center justify-between text-xs">
          <span class="text-[11px] text-stone-400">
            存证来源：{{ record.rawText || '结构化规则引擎审计' }}
          </span>
          <button 
            @click="loadToAudit(record)"
            class="text-xs bg-stone-100 hover:bg-emerald-50 text-stone-700 hover:text-emerald-700 border border-stone-300 hover:border-emerald-400 px-3 py-1 rounded-lg transition-colors flex items-center space-x-1"
          >
            <span>🔄</span>
            <span>载入复查 / 重新推演</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api, AuditRecordItem, HerbItem } from '@/api'

const emit = defineEmits<{
  (e: 'load-prescription', herbs: HerbItem[]): void
}>()

const records = ref<AuditRecordItem[]>([])
const isLoading = ref(false)
const currentFilter = ref<'ALL' | 'DANGER' | 'WARNING' | 'SAFE'>('ALL')

const filterOptions = [
  { key: 'ALL' as const, label: '全部存证' },
  { key: 'DANGER' as const, label: '🚨 违规拦截' },
  { key: 'WARNING' as const, label: '⚠️ 剂量预警' },
  { key: 'SAFE' as const, label: '🛡️ 完全合规' },
]

const fetchHistory = async () => {
  isLoading.value = true
  try {
    const res = await api.getAuditHistory(30)
    records.value = res.data
  } catch (e) {
    console.error('获取历史审计失败', e)
  } finally {
    isLoading.value = false
  }
}

const getCountByStatus = (statusKey: 'ALL' | 'DANGER' | 'WARNING' | 'SAFE') => {
  if (statusKey === 'ALL') return records.value.length
  return records.value.filter(r => r.safetyStatus === statusKey).length
}

const filteredRecords = computed(() => {
  if (currentFilter.value === 'ALL') return records.value
  return records.value.filter(r => r.safetyStatus === currentFilter.value)
})

const parseHerbs = (herbsJson: string): HerbItem[] => {
  try {
    if (!herbsJson) return []
    return JSON.parse(herbsJson)
  } catch {
    return []
  }
}

const parseContraindications = (jsonStr?: string): any[] => {
  try {
    if (!jsonStr) return []
    return JSON.parse(jsonStr)
  } catch {
    return []
  }
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return '--'
  return timeStr.replace('T', ' ').substring(0, 19)
}

const getStatusBadgeClass = (status: string) => {
  if (status === 'DANGER') return 'bg-rose-100 text-rose-800 border border-rose-300'
  if (status === 'WARNING') return 'bg-amber-100 text-amber-800 border border-amber-300'
  return 'bg-emerald-100 text-emerald-800 border border-emerald-300'
}

const getStatusIcon = (status: string) => {
  if (status === 'DANGER') return '🚨'
  if (status === 'WARNING') return '⚠️'
  return '🛡️'
}

const getStatusText = (status: string) => {
  if (status === 'DANGER') return '存在法定配伍禁忌 (已拦截)'
  if (status === 'WARNING') return '存在极量超标/特殊煎服提示'
  return '处方安全审查通过'
}

const getCardBorderClass = (status: string) => {
  if (status === 'DANGER') return 'border-rose-200 hover:border-rose-400'
  if (status === 'WARNING') return 'border-amber-200 hover:border-amber-400'
  return 'border-stone-200 hover:border-emerald-300'
}

const loadToAudit = (record: AuditRecordItem) => {
  const herbs = parseHerbs(record.detectedHerbs)
  if (herbs.length > 0) {
    emit('load-prescription', herbs)
  }
}

onMounted(() => {
  fetchHistory()
})
</script>
