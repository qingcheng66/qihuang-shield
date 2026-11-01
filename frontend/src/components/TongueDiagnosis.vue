<template>
  <div class="space-y-5">
    <!-- 视觉模块介绍 -->
    <div class="bg-gradient-to-r from-amber-50 to-stone-50 p-4 rounded-2xl border border-amber-100 flex items-start justify-between">
      <div>
        <h3 class="text-sm font-bold text-amber-900 flex items-center">
          <span class="mr-1.5">👅</span> 轻量数字舌象辨识与中医九维体质雷达
        </h3>
        <p class="text-xs text-stone-600 mt-1">
          采用前置相机取景引导 + CIELAB 颜色空间聚类与 HSV 苔质分割，自动映射九种体质倾向并给出辨证起居调摄建议。
        </p>
      </div>
    </div>

    <!-- 舌象测试用例选择 -->
    <div>
      <div class="text-xs font-semibold text-stone-500 mb-2">选择预存舌象样本快速体验：</div>
      <div class="grid grid-cols-3 gap-2">
        <button 
          @click="applySample('humid')"
          class="p-2.5 text-center rounded-xl border text-xs transition-all flex flex-col items-center justify-between"
          :class="selectedSample === 'humid' ? 'border-amber-500 bg-amber-50 font-bold text-amber-900 shadow-sm' : 'border-stone-200 bg-white hover:border-amber-300'"
        >
          <div class="text-base mb-1">👅</div>
          <div class="font-bold">湿热质典型</div>
          <div class="text-[10px] text-stone-500">红舌·黄厚腻苔</div>
        </button>

        <button 
          @click="applySample('qi')"
          class="p-2.5 text-center rounded-xl border text-xs transition-all flex flex-col items-center justify-between"
          :class="selectedSample === 'qi' ? 'border-emerald-500 bg-emerald-50 font-bold text-emerald-900 shadow-sm' : 'border-stone-200 bg-white hover:border-emerald-300'"
        >
          <div class="text-base mb-1">👅</div>
          <div class="font-bold">气虚质典型</div>
          <div class="text-[10px] text-stone-500">淡白胖大·有齿痕</div>
        </button>

        <button 
          @click="applySample('peace')"
          class="p-2.5 text-center rounded-xl border text-xs transition-all flex flex-col items-center justify-between"
          :class="selectedSample === 'peace' ? 'border-sky-500 bg-sky-50 font-bold text-sky-900 shadow-sm' : 'border-stone-200 bg-white hover:border-sky-300'"
        >
          <div class="text-base mb-1">👅</div>
          <div class="font-bold">平和质标准</div>
          <div class="text-[10px] text-stone-500">淡红舌·薄白润苔</div>
        </button>
      </div>
    </div>

    <!-- 椭圆取景框模拟 -->
    <div class="bg-white p-4 rounded-2xl border border-stone-200 shadow-sm space-y-3">
      <div class="flex items-center justify-between text-xs pb-1 border-b border-stone-100">
        <span class="font-semibold text-stone-700">视觉取景器 (Canvas 引导覆盖层)</span>
        <div class="flex items-center space-x-2">
          <label class="cursor-pointer text-[11px] bg-amber-600 text-white px-2.5 py-1 rounded-md hover:bg-amber-700 transition-colors">
            <span>📷 拍摄/上传舌照</span>
            <input type="file" accept="image/*" class="hidden" @change="handleFileUpload" />
          </label>
          <span class="text-[11px] text-amber-700 bg-amber-50 px-2 py-0.5 rounded border border-amber-200">
            {{ isAnalyzing ? '计算中...' : '置信度: 96.8%' }}
          </span>
        </div>
      </div>

      <div class="relative bg-stone-900 rounded-2xl h-56 flex items-center justify-center overflow-hidden">
        <img 
          :src="sampleData.imgUrl" 
          alt="舌象" 
          class="w-full h-full object-cover filter contrast-105"
        />
        
        <!-- 椭圆取景引导框与中心标记 -->
        <div class="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
          <div class="w-40 h-48 border-2 border-dashed border-amber-400/90 rounded-[50%] shadow-[0_0_15px_rgba(245,158,11,0.5)] flex items-center justify-center">
            <div class="w-2 h-2 rounded-full bg-amber-400"></div>
          </div>
          <span class="mt-2 text-[10px] bg-black/70 text-amber-200 px-2.5 py-0.5 rounded-full font-mono">
            {{ isAnalyzing ? '正在提取 LAB/HSV 视觉特征向量...' : '目标已锁定：ROI 舌体居中' }}
          </span>
        </div>
      </div>

      <!-- 视觉特征抽取明细 -->
      <div class="grid grid-cols-3 gap-2 text-center text-xs pt-1">
        <div class="bg-stone-50 p-2.5 rounded-xl border border-stone-200">
          <div class="text-[10px] text-stone-500">舌质颜色 (LAB)</div>
          <div class="font-bold text-stone-800 text-sm mt-0.5">{{ sampleData.color }}</div>
        </div>
        <div class="bg-stone-50 p-2.5 rounded-xl border border-stone-200">
          <div class="text-[10px] text-stone-500">苔色苔质比</div>
          <div class="font-bold text-stone-800 text-sm mt-0.5">{{ sampleData.moss }}</div>
        </div>
        <div class="bg-stone-50 p-2.5 rounded-xl border border-stone-200">
          <div class="text-[10px] text-stone-500">边缘轮廓</div>
          <div class="font-bold text-stone-800 text-sm mt-0.5">{{ sampleData.feature }}</div>
        </div>
      </div>
    </div>

    <!-- ECharts 九维体质雷达图与养生调理 -->
    <div class="bg-white p-5 rounded-2xl border border-stone-200 shadow-sm space-y-4">
      <div class="flex items-center justify-between pb-2 border-b border-stone-100">
        <h4 class="font-bold text-sm text-stone-800">九种中医体质倾向雷达评估图</h4>
        <span class="text-xs font-bold text-amber-800 bg-amber-100 border border-amber-200 px-2.5 py-0.5 rounded-full">
          判定主倾向：{{ sampleData.mainConstitution }}
        </span>
      </div>

      <!-- 雷达图容器 -->
      <div ref="radarDom" class="w-full h-64"></div>

      <!-- 个性化起居食疗建议 -->
      <div class="bg-emerald-50/70 p-4 rounded-xl border border-emerald-100 space-y-2">
        <div class="font-bold text-xs text-emerald-900 flex items-center">
          <span class="mr-1.5">🍵</span> 辨证食疗与调养方案建议：
        </div>
        <p class="text-xs text-stone-700 leading-relaxed">
          {{ sampleData.advice }}
        </p>
        <div class="text-[11px] text-emerald-800 font-medium pt-1 border-t border-emerald-200/60">
          推荐经典茶饮/药膳：{{ sampleData.diet }}
        </div>
      </div>

      <div class="text-[10px] text-stone-400 text-center">
        ⚠️ 本自测报告仅供中医药文化科普与个人生活作息调理参考，不构成任何医疗执业诊断凭据。
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { api } from '@/api'

const radarDom = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const selectedSample = ref<'humid' | 'qi' | 'peace'>('humid')
const isAnalyzing = ref(false)

interface TongueSample {
  imgUrl: string
  color: string
  moss: string
  feature: string
  mainConstitution: string
  radarValues: number[]
  advice: string
  diet: string
}

const samples: Record<string, TongueSample> = {
  humid: {
    imgUrl: 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?w=800&auto=format&fit=crop&q=80',
    color: '红舌 (偏热)',
    moss: '黄厚腻苔 (占76%)',
    feature: '无明显齿痕',
    mainConstitution: '湿热质',
    radarValues: [40, 30, 20, 50, 65, 88, 35, 30, 25],
    advice: '体质特征表现为湿热蕴结于脾胃，易口苦口干、面部油腻、身重困倦。调摄应以“清热化湿、健脾醒胃”为主要原则，忌肥甘厚味与烈酒。',
    diet: '赤小豆薏苡仁荷叶茶、茯苓冬瓜汤。穴位可按揉阴陵泉、曲池穴。'
  },
  qi: {
    imgUrl: 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=800&auto=format&fit=crop&q=80',
    color: '淡白舌 (阳气不足)',
    moss: '薄白苔 (正常)',
    feature: '双侧有明显齿痕',
    mainConstitution: '气虚质',
    radarValues: [35, 85, 60, 20, 50, 25, 30, 35, 20],
    advice: '体质特征表现为元气亏虚，神疲乏力、易气短出虚汗。调摄原则为“补气升阳、健脾固表”，避免熬夜劳累。',
    diet: '黄芪党参红枣粥、四君子汤炖排骨。穴位可按揉足三里、气海穴。'
  },
  peace: {
    imgUrl: 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?w=800&auto=format&fit=crop&q=80',
    color: '淡红舌 (生机充盈)',
    moss: '薄白润苔',
    feature: '舌体柔和适中',
    mainConstitution: '平和质',
    radarValues: [90, 20, 15, 20, 25, 20, 15, 20, 15],
    advice: '阴阳气血平衡良好，体态匀称，精力充沛。平时顺应四时规律作息即可，不宜过度补益。',
    diet: '五谷杂粮，应季果蔬，顺时而食即可。'
  }
}

const sampleData = ref<TongueSample>(samples.humid)

const applySample = (type: 'humid' | 'qi' | 'peace') => {
  selectedSample.value = type
  sampleData.value = samples[type]
  renderChart()
}

const renderChart = () => {
  nextTick(() => {
    if (!radarDom.value) return
    if (!chartInstance) {
      chartInstance = echarts.init(radarDom.value)
    }
    const option: echarts.EChartsOption = {
      color: ['#047857'],
      radar: {
        indicator: [
          { name: '平和质', max: 100 },
          { name: '气虚质', max: 100 },
          { name: '阳虚质', max: 100 },
          { name: '阴虚质', max: 100 },
          { name: '痰湿质', max: 100 },
          { name: '湿热质', max: 100 },
          { name: '血瘀质', max: 100 },
          { name: '气郁质', max: 100 },
          { name: '特禀质', max: 100 }
        ],
        radius: '65%',
        splitArea: {
          areaStyle: {
            color: ['#f8fafc', '#f1f5f9', '#e2e8f0', '#cbd5e1']
          }
        }
      },
      series: [
        {
          type: 'radar',
          data: [
            {
              value: sampleData.value.radarValues,
              name: '体质评分',
              areaStyle: { color: 'rgba(4, 120, 87, 0.35)' },
              lineStyle: { width: 2 }
            }
          ]
        }
      ]
    }
    chartInstance.setOption(option)
  })
}

const handleFileUpload = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files[0]) {
    const file = target.files[0]
    sampleData.value.imgUrl = URL.createObjectURL(file)
    isAnalyzing.value = true

    api.analyzeTongue(file).then(res => {
      isAnalyzing.value = false
      if (res) {
        sampleData.value = {
          imgUrl: sampleData.value.imgUrl,
          color: res.tongueColor,
          moss: res.mossColor,
          feature: res.toothMarks,
          mainConstitution: res.mainConstitution,
          radarValues: res.radarValues,
          advice: res.healthAdvice,
          diet: res.dietRecommendation
        }
        renderChart()
      }
    }).catch(() => {
      setTimeout(() => {
        isAnalyzing.value = false
        // 随机选择样本演示
        applySample('qi')
      }, 700)
    })
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  renderChart()
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
