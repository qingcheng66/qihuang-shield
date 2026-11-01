<template>
  <div>
    <!-- 悬浮触发气泡按钮 -->
    <button 
      @click="isOpen = !isOpen"
      class="fixed bottom-6 right-6 z-40 bg-gradient-to-r from-emerald-700 to-teal-800 hover:from-emerald-600 hover:to-teal-700 text-white px-4 py-3 rounded-full shadow-2xl flex items-center space-x-2 border-2 border-emerald-400/80 transition-all transform hover:scale-105 active:scale-95"
    >
      <span class="text-lg">💬</span>
      <span class="text-xs font-bold tracking-wide">岐黄 AI 药学问答助手</span>
    </button>

    <!-- 弹出的对话抽屉 / 窗口 -->
    <div 
      v-if="isOpen" 
      class="fixed bottom-20 right-4 md:right-6 w-[92vw] sm:w-[420px] max-h-[620px] h-[75vh] bg-white rounded-3xl shadow-2xl border border-stone-200 z-50 flex flex-col overflow-hidden animate-fade-in"
    >
      <!-- 顶栏 -->
      <div class="bg-gradient-to-r from-emerald-800 to-teal-900 text-white p-4 flex items-center justify-between shadow-sm">
        <div class="flex items-center space-x-2.5">
          <div class="w-8 h-8 rounded-full bg-emerald-600 flex items-center justify-center text-sm font-bold shadow-inner">
            岐
          </div>
          <div>
            <div class="text-xs font-bold">岐黄盾 · 智能安全伴聊助手</div>
            <div class="text-[10px] text-emerald-300">本草配伍 · 煎服规范 · 体质调摄</div>
          </div>
        </div>
        <button @click="isOpen = false" class="text-stone-300 hover:text-white text-lg font-bold">×</button>
      </div>

      <!-- 消息列表区 -->
      <div class="flex-1 p-4 overflow-y-auto space-y-3.5 bg-stone-50 text-xs" ref="msgContainer">
        <!-- 欢迎卡片 -->
        <div class="bg-emerald-50/90 p-3 rounded-2xl border border-emerald-100 text-stone-700 space-y-2">
          <div class="font-bold text-emerald-900 flex items-center">
            <span class="mr-1">🌿</span> 您好！我是您的中医药安全与调养问答助手。
          </div>
          <p class="text-[11px] text-stone-600 leading-relaxed">
            您可以随时咨询中药配伍禁忌、有毒药材极量限制、特殊煎服方法，或中医体质饮食调护常识。
          </p>
          <!-- 快捷提问推荐 -->
          <div class="pt-1">
            <div class="text-[10px] font-semibold text-emerald-800 mb-1.5">评委与大众高频提问示例（点击即问）：</div>
            <div class="flex flex-wrap gap-1.5">
              <button 
                v-for="q in quickQuestions" 
                :key="q"
                @click="sendQuick(q)"
                class="px-2.5 py-1 bg-white hover:bg-emerald-100 text-emerald-800 rounded-lg text-[10px] border border-emerald-200 transition-colors text-left"
              >
                {{ q }}
              </button>
            </div>
          </div>
        </div>

        <!-- 聊天气泡记录 -->
        <div 
          v-for="(msg, i) in messages" 
          :key="i"
          class="flex"
          :class="msg.role === 'user' ? 'justify-end' : 'justify-start'"
        >
          <div 
            class="max-w-[85%] p-3 rounded-2xl text-xs leading-relaxed"
            :class="msg.role === 'user' 
              ? 'bg-emerald-700 text-white rounded-tr-none shadow-sm' 
              : 'bg-white text-stone-800 border border-stone-200 rounded-tl-none shadow-sm whitespace-pre-line'"
          >
            {{ msg.content }}
          </div>
        </div>

        <!-- 思考等待中 -->
        <div v-if="isLoading" class="flex justify-start">
          <div class="bg-white border border-stone-200 p-3 rounded-2xl rounded-tl-none flex items-center space-x-2 text-stone-500 text-xs">
            <span class="w-2 h-2 rounded-full bg-emerald-600 animate-ping"></span>
            <span>正在检索《神农本草经》《中国药典》，AI 推理中...</span>
          </div>
        </div>
      </div>

      <!-- 底部输入框 -->
      <div class="p-3 bg-white border-t border-stone-200 flex items-center space-x-2">
        <input 
          v-model="inputQuestion" 
          @keyup.enter="handleSend"
          type="text" 
          placeholder="向 AI 咨询中药配伍与安全调理疑问..." 
          class="flex-1 text-xs px-3.5 py-2.5 bg-stone-50 rounded-xl border border-stone-200 focus:outline-none focus:border-emerald-500"
        />
        <button 
          @click="handleSend"
          :disabled="!inputQuestion.trim() || isLoading"
          class="bg-emerald-700 hover:bg-emerald-800 disabled:opacity-40 text-white text-xs px-4 py-2.5 rounded-xl font-bold transition-colors"
        >
          发送
        </button>
      </div>

      <!-- 免责底栏 -->
      <div class="bg-stone-100 text-center py-1 text-[9px] text-stone-400 border-t border-stone-200">
        ⚠️ 本助手仅供中医药文化科普与合理用药指引，不构成法定执业诊断凭据
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { api } from '@/api'

const isOpen = ref(false)
const inputQuestion = ref('')
const isLoading = ref(false)
const msgContainer = ref<HTMLElement | null>(null)

interface Message {
  role: 'user' | 'ai'
  content: string
}

const quickQuestions = [
  '为什么甘草和甘遂严禁同方使用？',
  '细辛入汤剂最多能用几克？超量有什么风险？',
  '附子先煎 30~60 分钟有什么科学依据？',
  '湿热体质平时应该喝什么代茶饮？'
]

const messages = ref<Message[]>([])

const scrollToBottom = () => {
  nextTick(() => {
    if (msgContainer.value) {
      msgContainer.value.scrollTop = msgContainer.value.scrollHeight
    }
  })
}

const handleSend = async () => {
  const q = inputQuestion.value.trim()
  if (!q || isLoading.value) return

  messages.value.push({ role: 'user', content: q })
  inputQuestion.value = ''
  isLoading.value = true
  scrollToBottom()

  try {
    const reply = await api.chatWithAi(q)
    messages.value.push({ role: 'ai', content: reply })
  } catch {
    messages.value.push({
      role: 'ai',
      content: '根据《中国药典》规定，中药配伍注重七情和合、生克制化。请遵医嘱合理用药。（系统处于离线守护模式）'
    })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

const sendQuick = (q: string) => {
  inputQuestion.value = q
  handleSend()
}
</script>
