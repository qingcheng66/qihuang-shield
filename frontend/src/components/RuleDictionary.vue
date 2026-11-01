<template>
  <div class="space-y-4">
    <!-- 顶栏说明 -->
    <div class="bg-white p-4 rounded-2xl border border-stone-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-3">
      <div>
        <h3 class="text-sm font-bold text-stone-800 flex items-center">
          <span class="mr-1.5">📖</span> 《中国药典》法定配伍禁忌规则库 (系统内嵌)
        </h3>
        <p class="text-xs text-stone-500 mt-0.5">
          由 Java 规则引擎在服务启动时一次性预载至内存 Set 字典中，确保并发拦截延迟 &lt; 50 微秒。
        </p>
      </div>

      <!-- 搜索过滤 -->
      <div class="w-full md:w-64">
        <input 
          v-model="searchTerm" 
          type="text" 
          placeholder="快速检索药名/禁忌 (如: 细辛, 甘草)..." 
          class="w-full text-xs px-3 py-1.5 rounded-lg bg-stone-50 border border-stone-200 focus:outline-none focus:border-emerald-500"
        />
      </div>
    </div>

    <!-- 规则字典网格 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      
      <!-- 1. 十八反 (相反为剧毒) -->
      <div class="bg-white p-4 rounded-2xl border border-rose-200 shadow-sm space-y-3">
        <div class="flex items-center justify-between pb-2 border-b border-rose-100">
          <div class="font-bold text-xs text-rose-800 flex items-center">
            <span class="mr-1 text-sm">🚨</span> 十八反歌诀与三大核心冲突药组 (相反为剧毒)
          </div>
          <span class="text-[10px] bg-rose-100 text-rose-700 px-2 py-0.5 rounded font-bold">法定严禁同方</span>
        </div>

        <div class="space-y-2">
          <div 
            v-for="(group, idx) in filteredShiBaFan" 
            :key="idx" 
            class="p-3 bg-rose-50/50 rounded-xl border border-rose-100 space-y-1.5"
          >
            <div class="font-bold text-rose-900 text-xs flex items-center justify-between">
              <span>【{{ group.name }}】{{ group.leader }}</span>
              <span class="text-[10px] text-stone-500">反 {{ group.conflicts.length }} 味中药</span>
            </div>
            <div class="flex flex-wrap gap-1">
              <span 
                v-for="c in group.conflicts" 
                :key="c" 
                class="px-2 py-0.5 bg-white text-rose-800 rounded text-[11px] font-medium border border-rose-200"
              >
                ❌ {{ c }}
              </span>
            </div>
            <p class="text-[11px] text-stone-600 italic pt-1">
              📜 {{ group.quote }}
            </p>
            <p class="text-[11px] text-rose-700 font-mono">
              💡 {{ group.mechanism }}
            </p>
          </div>
        </div>
      </div>

      <!-- 2. 十九畏 (相畏相恶减效) -->
      <div class="bg-white p-4 rounded-2xl border border-amber-200 shadow-sm space-y-3">
        <div class="flex items-center justify-between pb-2 border-b border-amber-100">
          <div class="font-bold text-xs text-amber-800 flex items-center">
            <span class="mr-1 text-sm">⚠️</span> 十九畏歌诀 (相克减效与毒性增强)
          </div>
          <span class="text-[10px] bg-amber-100 text-amber-700 px-2 py-0.5 rounded font-bold">9组经典相恶</span>
        </div>

        <div class="grid grid-cols-1 gap-2">
          <div 
            v-for="(wei, idx) in filteredShiJiuWei" 
            :key="idx" 
            class="p-2.5 bg-amber-50/50 rounded-xl border border-amber-100 text-xs space-y-0.5"
          >
            <div class="font-bold text-amber-900">
              ⚡ {{ wei.pair }}
            </div>
            <p class="text-[11px] text-stone-600">{{ wei.desc }}</p>
          </div>
        </div>
      </div>

      <!-- 3. 药典极量红线 -->
      <div class="bg-white p-4 rounded-2xl border border-stone-200 shadow-sm space-y-3">
        <div class="flex items-center justify-between pb-2 border-b border-stone-100">
          <div class="font-bold text-xs text-stone-800 flex items-center">
            <span class="mr-1 text-sm">⚖️</span> 《中国药典》单剂极量红线控制表
          </div>
          <span class="text-[10px] bg-stone-100 text-stone-700 px-2 py-0.5 rounded">毒性蓄积警戒</span>
        </div>

        <div class="space-y-2">
          <div 
            v-for="(dose, idx) in filteredDosageLimits" 
            :key="idx" 
            class="p-2.5 bg-stone-50 rounded-xl border border-stone-200 text-xs space-y-1"
          >
            <div class="flex items-center justify-between">
              <span class="font-bold text-stone-900">{{ dose.herb }}</span>
              <span class="px-2 py-0.5 bg-rose-100 text-rose-700 rounded font-mono font-bold text-[11px]">
                药典极量: {{ dose.limit }}
              </span>
            </div>
            <p class="text-[11px] text-stone-600">{{ dose.warning }}</p>
          </div>
        </div>
      </div>

      <!-- 4. 特殊煎服法指引 -->
      <div class="bg-white p-4 rounded-2xl border border-sky-200 shadow-sm space-y-3">
        <div class="flex items-center justify-between pb-2 border-b border-sky-100">
          <div class="font-bold text-xs text-sky-800 flex items-center">
            <span class="mr-1 text-sm">ℹ️</span> 中药特殊煎服规范体系
          </div>
          <span class="text-[10px] bg-sky-100 text-sky-700 px-2 py-0.5 rounded">科学溶出与减毒</span>
        </div>

        <div class="space-y-2">
          <div 
            v-for="(dec, idx) in filteredDecoctions" 
            :key="idx" 
            class="p-2.5 bg-sky-50/50 rounded-xl border border-sky-100 text-xs space-y-1"
          >
            <div class="flex items-center justify-between">
              <span class="font-bold text-sky-900">【{{ dec.method }}】代表药材：{{ dec.herbs }}</span>
            </div>
            <p class="text-[11px] text-stone-600">{{ dec.reason }}</p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api } from '@/api'

const searchTerm = ref('')

const shiBaFanData = ref([
  {
    name: '甘草反组',
    leader: '甘草',
    conflicts: ['甘遂', '大戟', '海藻', '芫花'],
    quote: '《本草纲目》：甘草反甘遂、大戟、海藻、芫花。相反者，激而生变，不可同用。',
    mechanism: '甘草酸抑制肠道 P-糖蛋白外排泵，使甘遂、芫花等二萜毒性成分体内蓄积量剧增，诱发剧烈水泻与心脏停搏。'
  },
  {
    name: '乌头反组 (川乌/草乌/附子)',
    leader: '附子 / 川乌 / 草乌',
    conflicts: ['半夏', '瓜蒌', '贝母', '白蔹', '白及'],
    quote: '《儒门事亲》：本草明言十八反，半蒌贝蔹及攻乌。',
    mechanism: '乌头碱水解产物与半夏、贝母等生物碱成分协同兴奋迷走神经并诱发严重心室纤颤。'
  },
  {
    name: '藜芦反组',
    leader: '藜芦',
    conflicts: ['人参', '党参', '沙参', '丹参', '玄参', '苦参', '细辛', '芍药'],
    quote: '《神农本草经》：诸参辛芍叛藜芦。',
    mechanism: '藜芦含大量甾体生物碱，与人参皂苷及芍药苷同服显著抑制延髓呼吸中枢并恶化休克。'
  }
])

const shiJiuWeiData = ref([
  { pair: '硫黄畏朴硝', desc: '硫黄与朴硝同用，生成毒性硫化物' },
  { pair: '水银畏砒霜', desc: '剧毒汞砷化合物，同用脏器迅速衰竭' },
  { pair: '狼毒畏密陀僧', desc: '铅毒与根茎毒素叠加致中毒性肝炎' },
  { pair: '巴豆畏牵牛', desc: '剧烈峻下泻水，同用致脱水休克与肠穿孔' },
  { pair: '丁香畏郁金', desc: '芳香开窍与清心凉血相抵，减效增燥' },
  { pair: '川乌草乌畏犀角', desc: '大热与大寒相反激变，损伤心阳' },
  { pair: '芒硝畏三棱', desc: '剧烈破血与软坚相撞，诱发出血' },
  { pair: '官桂畏赤石脂', desc: '肉桂温阳散寒被石脂固涩吸附减效' },
  { pair: '人参畏五灵脂', desc: '人参益气与五灵脂活血化瘀相争，减弱补虚疗效' }
])

const dosageLimitsData = ref([
  { herb: '细辛', limit: '3g', warning: '含黄樟醚及马兜铃酸，超量抑制呼吸中枢与肾蓄积，“细辛不过钱”' },
  { herb: '附子 / 制附子', limit: '15g', warning: '双酯型乌头碱致心律失常与房室传导阻滞，必须文火先煎 30~60 分钟' },
  { herb: '巴豆', limit: '0.3g', warning: '含巴豆油及巴豆毒素，致剧烈腐蚀性胃肠炎' },
  { herb: '马钱子', limit: '0.6g', warning: '士的宁中枢强毒性，导致角弓反张与强直性惊厥' },
  { herb: '甘遂', limit: '1.5g', warning: '二萜刺激性树脂致急性水样泻脱水' },
  { herb: '洋金花', limit: '0.6g', warning: '东莨菪碱致中枢抗胆碱综合征、幻觉昏迷' }
])

const decoctionsData = ref([
  { method: '先煎', herbs: '生石膏、磁石、制附子、生川乌', reason: '矿物贝壳质坚难溶需延长溶出；乌头类需高温水解消除毒性。文火先煎30~60分钟。' },
  { method: '后下', herbs: '薄荷、砂仁、沉香、钩藤、大黄', reason: '含挥发油或受热易破坏成分，在群药煎毕前5~10分钟下锅。' },
  { method: '包煎', herbs: '辛夷、旋覆花、车前子、滑石、蒲黄', reason: '含绒毛刺激咽喉引起剧烈咳嗽，或细小种子黏锅底焦糊，需用纱布袋包好入煎。' },
  { method: '烊化', herbs: '阿胶、鹿角胶、龟甲胶、蜂蜜', reason: '胶质浓稠黏腻，同煎易黏锅糊底且破坏药效，需隔水加热溶化或用药汁冲服。' }
])

// 过滤计算属性
const filteredShiBaFan = computed(() => {
  const term = searchTerm.value.trim()
  if (!term) return shiBaFanData.value
  return shiBaFanData.value.filter(g => 
    g.name.includes(term) || g.leader.includes(term) || g.conflicts.some(c => c.includes(term))
  )
})

const filteredShiJiuWei = computed(() => {
  const term = searchTerm.value.trim()
  if (!term) return shiJiuWeiData.value
  return shiJiuWeiData.value.filter(w => w.pair.includes(term) || w.desc.includes(term))
})

const filteredDosageLimits = computed(() => {
  const term = searchTerm.value.trim()
  if (!term) return dosageLimitsData.value
  return dosageLimitsData.value.filter(d => d.herb.includes(term) || d.warning.includes(term))
})

const filteredDecoctions = computed(() => {
  const term = searchTerm.value.trim()
  if (!term) return decoctionsData.value
  return decoctionsData.value.filter(d => d.method.includes(term) || d.herbs.includes(term))
})

onMounted(async () => {
  try {
    const res = await api.getAllRules()
    if (res) {
      if (res.shiBaFan) shiBaFanData.value = res.shiBaFan
      if (res.shiJiuWei) shiJiuWeiData.value = res.shiJiuWei
      if (res.dosageLimits) dosageLimitsData.value = res.dosageLimits
      if (res.decoctions) decoctionsData.value = res.decoctions
    }
  } catch {
    // 使用离线字典
  }
})
</script>
