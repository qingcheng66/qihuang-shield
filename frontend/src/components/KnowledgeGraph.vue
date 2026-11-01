<template>
  <div class="space-y-4">
    <!-- 图谱顶栏操作与图例 -->
    <div class="bg-white p-4 rounded-2xl border border-stone-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-3">
      <div>
        <h3 class="text-sm font-bold text-stone-800 flex items-center">
          <span class="mr-1.5">🕸️</span> 本草、方剂、归经与禁忌三维力导向图谱
        </h3>
        <p class="text-xs text-stone-500 mt-0.5">
          基于 ECharts 物理力导向引擎。支持自由拖拽、滑轮缩放与点击考据药典明细。
        </p>
      </div>

      <!-- 图谱图例 -->
      <div class="flex flex-wrap gap-2 text-[10px]">
        <span class="flex items-center px-2 py-0.5 bg-emerald-100 text-emerald-800 rounded">
          <span class="w-2 h-2 rounded-full bg-emerald-600 mr-1"></span> 药材 (Herb)
        </span>
        <span class="flex items-center px-2 py-0.5 bg-rose-100 text-rose-800 rounded">
          <span class="w-2 h-2 rounded-full bg-rose-600 mr-1"></span> 经典名方 (Formula)
        </span>
        <span class="flex items-center px-2 py-0.5 bg-amber-100 text-amber-800 rounded">
          <span class="w-2 h-2 rounded-full bg-amber-600 mr-1"></span> 性味归经 (Meridian)
        </span>
        <span class="flex items-center px-2 py-0.5 bg-cyan-100 text-cyan-800 rounded">
          <span class="w-2 h-2 rounded-full bg-cyan-600 mr-1"></span> 功效分类 (Action)
        </span>
        <span class="flex items-center px-2 py-0.5 bg-rose-50 text-rose-700 border border-rose-300 rounded font-bold">
          --- 十八反相克红线
        </span>
      </div>
    </div>

    <!-- 搜索过滤条 -->
    <div class="flex items-center space-x-2">
      <div class="relative flex-1">
        <input 
          v-model="searchQuery" 
          @input="handleSearch"
          type="text" 
          placeholder="搜索图谱节点 (如: 黄芪, 甘草, 细辛, 补中益气汤)..." 
          class="w-full text-xs px-3.5 py-2 rounded-xl bg-white border border-stone-200 focus:outline-none focus:border-emerald-500"
        />
        <button 
          v-if="searchQuery" 
          @click="clearSearch"
          class="absolute right-3 top-2 text-stone-400 hover:text-stone-600 text-xs"
        >
          ×
        </button>
      </div>
      <button 
        @click="resetZoom"
        class="text-xs bg-stone-100 hover:bg-stone-200 text-stone-700 px-3 py-2 rounded-xl border border-stone-200 transition-colors"
      >
        重置视角
      </button>
    </div>

    <!-- ECharts 关系图容器 -->
    <div class="bg-stone-900 rounded-2xl p-2 shadow-inner border border-stone-800 relative">
      <div ref="chartDom" class="w-full h-[450px] md:h-[540px]"></div>
      
      <!-- 悬浮操作提示 -->
      <div class="absolute bottom-4 left-4 bg-black/60 backdrop-blur-sm text-stone-300 text-[10px] px-3 py-1.5 rounded-lg border border-stone-700 pointer-events-none">
        💡 交互提示：按住鼠标左键可自由拖动节点；滚动滑轮缩放；点击节点呼出药典专论。
      </div>
    </div>

    <!-- 选中节点详情抽屉 / 卡片 -->
    <div v-if="selectedNode" class="bg-white p-4 rounded-xl border border-stone-200 text-xs space-y-2 shadow-sm animate-fade-in">
      <div class="flex items-center justify-between pb-2 border-b border-stone-100">
        <div class="flex items-center space-x-2">
          <span class="w-2.5 h-2.5 rounded-full" :class="selectedNodeCategoryColor"></span>
          <span class="font-bold text-sm text-stone-800">📖 本草考据：{{ selectedNode.name }}</span>
        </div>
        <button @click="selectedNode = null" class="text-stone-400 hover:text-stone-600 text-sm font-bold">×</button>
      </div>

      <div v-if="herbDetail" class="space-y-1.5 text-stone-700">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
          <div><span class="font-semibold text-stone-900">【性味】</span>{{ herbDetail.natureFlavor }}</div>
          <div><span class="font-semibold text-stone-900">【归经】</span>{{ herbDetail.meridian }}</div>
          <div><span class="font-semibold text-stone-900">【常用剂量】</span>{{ herbDetail.dosageRange }}</div>
          <div v-if="herbDetail.maxDosage"><span class="font-semibold text-rose-700">【药典极量】</span>{{ herbDetail.maxDosage }}g</div>
        </div>
        <div>
          <span class="font-semibold text-stone-900">【功效作用】</span>{{ herbDetail.efficacy }}
        </div>
        <div v-if="herbDetail.contraindications" class="text-rose-700">
          <span class="font-semibold">【配伍禁忌】</span>{{ herbDetail.contraindications }}
        </div>
        <div v-if="herbDetail.decoctionMethod" class="text-sky-700">
          <span class="font-semibold">【煎服要求】</span>{{ herbDetail.decoctionMethod }}
        </div>
        <div class="bg-stone-50 p-2.5 rounded-lg border border-stone-200 text-stone-600 italic mt-1">
          📜 {{ herbDetail.pharmacopoeiaExcerpt }}
        </div>
      </div>

      <div v-else class="text-stone-600 leading-relaxed">
        {{ selectedNode.detail }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onBeforeUnmount, computed } from 'vue'
import * as echarts from 'echarts'
import { api, GraphData, HerbDetail } from '@/api'

const chartDom = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const searchQuery = ref('')
const selectedNode = ref<any>(null)
const herbDetail = ref<HerbDetail | null>(null)

const defaultGraphData: GraphData = {
  categories: [
    { name: '中药材', itemStyle: { color: '#10b981' } },
    { name: '经典名方', itemStyle: { color: '#f43f5e' } },
    { name: '性味归经', itemStyle: { color: '#f59e0b' } },
    { name: '功效分类', itemStyle: { color: '#06b6d4' } }
  ],
  nodes: [
    { id: '1', name: '黄芪', symbolSize: 48, category: 0, detail: '《中国药典》甘，微温。归肺、脾经。功效：补气升阳，固表止汗，利水消肿。' },
    { id: '2', name: '当归', symbolSize: 42, category: 0, detail: '《中国药典》甘、辛，温。归肝、心、脾经。补血活血，调经止痛。' },
    { id: '3', name: '甘草', symbolSize: 48, category: 0, detail: '《中国药典》甘，平。归心、肺、脾、胃经。调和诸药。反甘遂、大戟、海藻、芫花！' },
    { id: '4', name: '甘遂', symbolSize: 34, category: 0, detail: '苦，寒；有毒。泻水逐饮。十八反：严禁与甘草配伍！' },
    { id: '5', name: '细辛', symbolSize: 34, category: 0, detail: '辛，温；有小毒。常用极量不超3g。“细辛不过钱”。反藜芦。' },
    { id: '6', name: '制附子', symbolSize: 40, category: 0, detail: '辛、甘，大热；有毒。回阳救逆。【先煎30~60分钟】。反半夏、贝母。' },
    { id: '7', name: '半夏', symbolSize: 36, category: 0, detail: '燥湿化痰，降逆止呕。十八反：反附子！' },
    { id: '8', name: '人参', symbolSize: 46, category: 0, detail: '大补元气，复脉固脱。反藜芦，畏五灵脂。' },
    { id: '9', name: '藜芦', symbolSize: 34, category: 0, detail: '辛、苦，寒；有毒。十八反：反诸参、细辛、芍药！' },
    { id: '10', name: '白芍', symbolSize: 38, category: 0, detail: '养血调经，敛阴柔肝。十八反：反藜芦。' },
    { id: '11', name: '丁香', symbolSize: 34, category: 0, detail: '温中降逆。十九畏：畏郁金。' },
    { id: '12', name: '郁金', symbolSize: 34, category: 0, detail: '活血止痛，行气解郁。十九畏：畏丁香。' },
    { id: '20', name: '补中益气汤', symbolSize: 56, category: 1, detail: '李东垣《脾胃论》名方。主治中气下陷。黄芪、当归、甘草。' },
    { id: '21', name: '当归补血汤', symbolSize: 50, category: 1, detail: '黄芪五两、当归一两，气旺生血名方。' },
    { id: '22', name: '四君子汤', symbolSize: 52, category: 1, detail: '人参、白术、茯苓、甘草。' },
    { id: '23', name: '四逆汤', symbolSize: 50, category: 1, detail: '附子、干姜、炙甘草。回阳救逆。' },
    { id: '30', name: '归肺、脾经', symbolSize: 36, category: 2, detail: '定向调节肺与脾运化系统。' },
    { id: '31', name: '归肝、肾经', symbolSize: 36, category: 2, detail: '定向调节肝藏血与肾藏精系统。' },
    { id: '40', name: '补气升阳', symbolSize: 36, category: 3, detail: '益气固表、提托中气。' },
    { id: '41', name: '补血活血', symbolSize: 36, category: 3, detail: '滋养阴血、活血化瘀。' }
  ],
  links: [
    { source: '1', target: '20', label: { show: true, formatter: '君药' } },
    { source: '2', target: '20', label: { show: true, formatter: '佐药' } },
    { source: '3', target: '20', label: { show: true, formatter: '使药' } },
    { source: '1', target: '21', label: { show: true, formatter: '主药(5:1)' } },
    { source: '2', target: '21' },
    { source: '8', target: '22', label: { show: true, formatter: '君药' } },
    { source: '3', target: '22', label: { show: true, formatter: '使药' } },
    { source: '6', target: '23', label: { show: true, formatter: '君药(先煎)' } },
    { source: '3', target: '23', label: { show: true, formatter: '使药' } },
    { source: '1', target: '30' },
    { source: '1', target: '40' },
    { source: '2', target: '31' },
    { source: '2', target: '41' },
    { 
      source: '3', 
      target: '4', 
      lineStyle: { color: '#ef4444', width: 3, type: 'dashed' },
      label: { show: true, formatter: '【十八反禁忌】' }
    },
    { 
      source: '6', 
      target: '7', 
      lineStyle: { color: '#ef4444', width: 3, type: 'dashed' },
      label: { show: true, formatter: '【十八反禁忌】' }
    },
    { 
      source: '8', 
      target: '9', 
      lineStyle: { color: '#ef4444', width: 3, type: 'dashed' },
      label: { show: true, formatter: '【十八反禁忌】' }
    },
    { 
      source: '5', 
      target: '9', 
      lineStyle: { color: '#ef4444', width: 3, type: 'dashed' },
      label: { show: true, formatter: '【十八反禁忌】' }
    },
    { 
      source: '10', 
      target: '9', 
      lineStyle: { color: '#ef4444', width: 3, type: 'dashed' },
      label: { show: true, formatter: '【十八反禁忌】' }
    },
    { 
      source: '11', 
      target: '12', 
      lineStyle: { color: '#ef4444', width: 3, type: 'dashed' },
      label: { show: true, formatter: '【十九畏相克】' }
    }
  ]
}

const currentGraph = ref<GraphData>(defaultGraphData)

const selectedNodeCategoryColor = computed(() => {
  if (!selectedNode.value) return 'bg-stone-400'
  const cat = selectedNode.value.category
  if (cat === 0) return 'bg-emerald-500'
  if (cat === 1) return 'bg-rose-500'
  if (cat === 2) return 'bg-amber-500'
  return 'bg-cyan-500'
})

const renderChart = () => {
  nextTick(() => {
    if (!chartDom.value) return
    if (!chartInstance) {
      chartInstance = echarts.init(chartDom.value)
      chartInstance.on('click', (params: any) => {
        if (params.dataType === 'node') {
          selectedNode.value = params.data
          // 异步查询详细中药考据
          api.getHerbDetail(params.data.name).then(res => {
            herbDetail.value = res
          }).catch(() => {
            herbDetail.value = null
          })
        }
      })
    }

    const option: echarts.EChartsOption = {
      tooltip: {
        formatter: (params: any) => {
          if (params.dataType === 'node') {
            return `<b>${params.data.name}</b><br/>${params.data.detail || ''}`
          }
          return params.name || '关联'
        }
      },
      legend: {
        textStyle: { color: '#ccc' },
        top: 10
      },
      series: [
        {
          type: 'graph',
          layout: 'force',
          data: currentGraph.value.nodes as any,
          links: currentGraph.value.links as any,
          categories: currentGraph.value.categories as any,
          roam: true,
          label: {
            show: true,
            position: 'right',
            formatter: '{b}',
            color: '#fff',
            fontSize: 12
          },
          lineStyle: {
            color: 'source',
            curveness: 0.15,
            width: 2
          },
          force: {
            repulsion: 380,
            edgeLength: [90, 160]
          }
        }
      ]
    }
    chartInstance.setOption(option)
  })
}

const loadGraphData = async () => {
  try {
    const data = await api.getGraphOverview()
    if (data && data.nodes && data.nodes.length > 0) {
      currentGraph.value = data
    }
  } catch {
    // 使用离线高保真图谱
  }
  renderChart()
}

const handleSearch = () => {
  if (!chartInstance) return
  const q = searchQuery.value.trim()
  if (!q) {
    chartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
    return
  }
  chartInstance.dispatchAction({
    type: 'highlight',
    seriesIndex: 0,
    name: q
  })
}

const clearSearch = () => {
  searchQuery.value = ''
  if (chartInstance) {
    chartInstance.dispatchAction({ type: 'downplay', seriesIndex: 0 })
  }
}

const resetZoom = () => {
  if (chartInstance) {
    chartInstance.dispatchAction({
      type: 'restore'
    })
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  loadGraphData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

defineExpose({
  resizeChart: handleResize
})
</script>
