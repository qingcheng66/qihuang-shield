<template>
  <div class="min-h-screen bg-stone-100 text-stone-800 antialiased font-sans flex flex-col">
    <!-- 顶部状态栏与视口切换器 -->
    <header class="bg-stone-900 text-white border-b border-stone-800 sticky top-0 z-50 shadow-md">
      <div class="max-w-7xl mx-auto px-4 h-14 flex items-center justify-between">
        <!-- 品牌标识 -->
        <div class="flex items-center space-x-3">
          <div class="w-8 h-8 rounded-lg bg-emerald-600 flex items-center justify-center font-bold text-white shadow-md text-base">
            盾
          </div>
          <div>
            <div class="flex items-center space-x-2">
              <span class="font-bold text-sm md:text-base tracking-wide text-white">岐黄盾 HerbShield</span>
              <span class="text-[10px] px-1.5 py-0.5 bg-emerald-950 text-emerald-400 border border-emerald-700 rounded font-mono">
                v1.0.0
              </span>
            </div>
            <p class="text-[10px] text-stone-400 hidden sm:block">南京中医药大学第十九届“挑战杯”竞赛 · 异构微服务用药安全哨兵平台</p>
          </div>
        </div>

        <!-- 顶栏微服务探活状态与视口切换 -->
        <div class="flex items-center space-x-3">
          <!-- 微服务状态 -->
          <div class="hidden md:flex items-center space-x-2 text-[11px] bg-stone-800 px-3 py-1 rounded-full border border-stone-700">
            <span class="flex items-center" :class="isJavaAlive ? 'text-emerald-400' : 'text-amber-400'">
              <span class="w-2 h-2 rounded-full mr-1.5" :class="isJavaAlive ? 'bg-emerald-400 animate-pulse' : 'bg-amber-400'"></span>
              Java :8080 规则{{ isJavaAlive ? '在线' : '本地' }}
            </span>
            <span class="text-stone-500">|</span>
            <span class="flex items-center text-emerald-400">
              <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse mr-1.5"></span>
              Python :8000 AI 就绪
            </span>
          </div>

          <!-- 视口模拟器切换 -->
          <div class="bg-stone-800 p-0.5 rounded-lg flex items-center border border-stone-700 text-xs">
            <button 
              @click="setMode('mobile')" 
              :class="viewMode === 'mobile' ? 'bg-emerald-700 text-white font-medium shadow' : 'text-stone-400 hover:text-white'"
              class="px-2.5 py-1 rounded-md transition-all flex items-center space-x-1"
            >
              <span>📱 手机模拟</span>
            </button>
            <button 
              @click="setMode('desktop')" 
              :class="viewMode === 'desktop' ? 'bg-emerald-700 text-white font-medium shadow' : 'text-stone-400 hover:text-white'"
              class="px-2.5 py-1 rounded-md transition-all flex items-center space-x-1"
            >
              <span>💻 PC宽屏大屏</span>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 主展示区 -->
    <main class="flex-1 py-3 md:py-6 px-2 md:px-6 flex justify-center items-start">
      <!-- 容器自适应控制：移动端模拟框 vs 宽屏控制台 -->
      <div 
        :class="viewMode === 'mobile' ? 'w-full max-w-md bg-stone-50 rounded-[36px] shadow-2xl border-8 border-stone-800 overflow-hidden min-h-[780px] flex flex-col' : 'w-full max-w-7xl bg-transparent flex flex-col'"
        class="transition-all duration-300"
      >
        <!-- 移动端外壳顶部刘海条 (仅 mobile 模式显示) -->
        <div v-if="viewMode === 'mobile'" class="h-6 bg-stone-800 flex justify-center items-center">
          <div class="w-24 h-3.5 bg-stone-900 rounded-b-xl flex justify-center items-center">
            <div class="w-2.5 h-2.5 rounded-full bg-stone-800 mr-2"></div>
            <div class="w-8 h-1 rounded-full bg-stone-800"></div>
          </div>
        </div>

        <!-- 内部应用头部 -->
        <div class="bg-emerald-800 text-white p-4 shadow-sm flex items-center justify-between">
          <div>
            <div class="text-xs text-emerald-200">中医药安全哨兵</div>
            <div class="text-base font-bold">岐黄盾 · 智能工作台</div>
          </div>
          <div class="text-right">
            <span class="inline-block text-[10px] bg-emerald-900/60 border border-emerald-500/30 px-2 py-0.5 rounded text-emerald-200">
              PWA 离线缓存支持
            </span>
          </div>
        </div>

        <!-- 功能选项卡导航 -->
        <div class="bg-white border-b border-stone-200 px-3 flex space-x-1 text-xs md:text-sm overflow-x-auto shadow-sm">
          <button 
            @click="switchTab('audit')"
            :class="activeTab === 'audit' ? 'border-b-2 border-emerald-600 text-emerald-800 font-bold bg-emerald-50/40' : 'text-stone-500 hover:text-stone-800'"
            class="py-3 px-3 transition-colors flex items-center space-x-1.5 whitespace-nowrap rounded-t-lg"
          >
            <span>📄</span>
            <span>处方配伍质检 (OCR+规则)</span>
          </button>
          <button 
            @click="switchTab('tongue')"
            :class="activeTab === 'tongue' ? 'border-b-2 border-emerald-600 text-emerald-800 font-bold bg-emerald-50/40' : 'text-stone-500 hover:text-stone-800'"
            class="py-3 px-3 transition-colors flex items-center space-x-1.5 whitespace-nowrap rounded-t-lg"
          >
            <span>👅</span>
            <span>数字望舌与体质 (CV+雷达)</span>
          </button>
          <button 
            @click="switchTab('graph')"
            :class="activeTab === 'graph' ? 'border-b-2 border-emerald-600 text-emerald-800 font-bold bg-emerald-50/40' : 'text-stone-500 hover:text-stone-800'"
            class="py-3 px-3 transition-colors flex items-center space-x-1.5 whitespace-nowrap rounded-t-lg"
          >
            <span>🕸️</span>
            <span>本草方剂知识图谱 (ECharts)</span>
          </button>
          <button 
            @click="switchTab('dictionary')"
            :class="activeTab === 'dictionary' ? 'border-b-2 border-emerald-600 text-emerald-800 font-bold bg-emerald-50/40' : 'text-stone-500 hover:text-stone-800'"
            class="py-3 px-3 transition-colors flex items-center space-x-1.5 whitespace-nowrap rounded-t-lg"
          >
            <span>📖</span>
            <span>内置禁忌字典库</span>
          </button>
        </div>

        <!-- 页面主工作区 -->
        <div class="p-4 md:p-6 flex-1 overflow-y-auto">
          <PrescriptionAudit v-if="activeTab === 'audit'" />
          <TongueDiagnosis ref="tongueComp" v-if="activeTab === 'tongue'" />
          <KnowledgeGraph ref="graphComp" v-if="activeTab === 'graph'" />
          <RuleDictionary v-if="activeTab === 'dictionary'" />
        </div>

        <!-- 移动端底部虚拟 Home Bar -->
        <div v-if="viewMode === 'mobile'" class="h-5 bg-stone-50 flex justify-center items-center pb-1">
          <div class="w-32 h-1 bg-stone-300 rounded-full"></div>
        </div>
      </div>
    </main>

    <!-- 底部状态声明 -->
    <footer class="bg-stone-900 text-stone-500 py-3 text-center text-[11px] border-t border-stone-800">
      南京中医药大学第十九届“挑战杯”大学生课外学术科技作品竞赛 · 科技发明制作 B 类 · 岐黄盾 (HerbShield)
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import PrescriptionAudit from '@/components/PrescriptionAudit.vue'
import TongueDiagnosis from '@/components/TongueDiagnosis.vue'
import KnowledgeGraph from '@/components/KnowledgeGraph.vue'
import RuleDictionary from '@/components/RuleDictionary.vue'
import { api } from '@/api'

const viewMode = ref<'desktop' | 'mobile'>('desktop')
const activeTab = ref<'audit' | 'tongue' | 'graph' | 'dictionary'>('audit')
const isJavaAlive = ref(true)

const tongueComp = ref<any>(null)
const graphComp = ref<any>(null)

const setMode = (mode: 'desktop' | 'mobile') => {
  viewMode.value = mode
  triggerResize()
}

const switchTab = (tab: 'audit' | 'tongue' | 'graph' | 'dictionary') => {
  activeTab.value = tab
  triggerResize()
}

const triggerResize = () => {
  setTimeout(() => {
    nextTick(() => {
      if (tongueComp.value && tongueComp.value.resizeChart) {
        tongueComp.value.resizeChart()
      }
      if (graphComp.value && graphComp.value.resizeChart) {
        graphComp.value.resizeChart()
      }
    })
  }, 200)
}

onMounted(async () => {
  isJavaAlive.value = await api.getHealth()
})
</script>
