import axios from 'axios'

const apiClient = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

export interface HerbItem {
  name: string
  dose: number
  unit?: string
  processing?: string
  decoctionMethod?: string
}

export interface ConflictWarning {
  type: string
  pair: string[]
  quote: string
  mechanism: string
  severity?: string
}

export interface OverdoseWarning {
  herb: string
  actualDose: number
  maxLimit: number
  warningText: string
}

export interface DecoctionNote {
  herb: string
  method: string
  instructions: string
}

export interface AuditResult {
  status: 'SAFE' | 'WARNING' | 'DANGER'
  summary: string
  herbCount?: number
  fatalWarnings: ConflictWarning[]
  overdoseWarnings: OverdoseWarning[]
  decoctionNotes: DecoctionNote[]
  executionTimeMs?: number
  auditTimestamp?: number
}

export interface TongueAnalysisResult {
  tongueColor: string
  mossColor: string
  toothMarks: string
  mainConstitution: string
  confidence: number
  radarValues: number[]
  healthAdvice: string
  dietRecommendation: string
}

export interface GraphNode {
  id: string
  name: string
  symbolSize: number
  category: number
  detail: string
}

export interface GraphLink {
  source: string
  target: string
  label?: { show: boolean; formatter: string }
  lineStyle?: { color?: string; width?: number; type?: string }
}

export interface GraphData {
  categories: Array<{ name: string; itemStyle: { color: string } }>
  nodes: GraphNode[]
  links: GraphLink[]
}

export interface HerbDetail {
  name: string
  pinyin: string
  natureFlavor: string
  meridian: string
  efficacy: string
  dosageRange: string
  maxDosage?: number
  contraindications: string
  decoctionMethod: string
  classicFormulas: string[]
  pharmacopoeiaExcerpt: string
}

export const api = {
  // 处方文本质检
  auditPrescriptionText: async (herbs: HerbItem[]): Promise<AuditResult> => {
    const res = await apiClient.post('/audit/prescription-text', { herbs })
    return res.data.data
  },

  // 处方图像全流程质检
  auditPrescriptionImage: async (file: File): Promise<any> => {
    const formData = new FormData()
    formData.append('file', file)
    const res = await apiClient.post('/audit/prescription-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return res.data.data
  },

  // 舌象分析
  analyzeTongue: async (file: File): Promise<TongueAnalysisResult> => {
    const formData = new FormData()
    formData.append('file', file)
    const res = await apiClient.post('/tongue/diagnose', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return res.data.data
  },

  // 知识图谱全景
  getGraphOverview: async (): Promise<GraphData> => {
    const res = await apiClient.get('/graph/overview')
    return res.data.data
  },

  // 药材本草考据
  getHerbDetail: async (name: string): Promise<HerbDetail> => {
    const res = await apiClient.get(`/graph/herb/${encodeURIComponent(name)}`)
    return res.data.data
  },

  // 法定字典规则全量
  getAllRules: async (): Promise<any> => {
    const res = await apiClient.get('/rules/all')
    return res.data.data
  },

  // 大模型 + RAG 深度方解推演
  getAiFormulaAnalysis: async (herbs: HerbItem[], auditResult: AuditResult): Promise<any> => {
    const res = await apiClient.post('/audit/ai-formula-analysis', { herbs, auditResult })
    return res.data.data
  },

  // 岐黄 AI 用药安全助手智能问答伴聊
  chatWithAi: async (question: string, contextHerbs?: string[]): Promise<string> => {
    const res = await apiClient.post('/ai/chat', { question, contextHerbs })
    return res.data.data.reply
  },

  // 服务心跳状态探活
  getHealth: async (): Promise<boolean> => {
    try {
      const res = await apiClient.get('/health', { timeout: 2000 })
      return res.status === 200
    } catch {
      return false
    }
  }
}
