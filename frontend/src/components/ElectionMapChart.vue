<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, shallowRef, toRef, watch } from 'vue'
import Highcharts from 'highcharts/highmaps'
import type { Options } from 'highcharts'
import HighchartsAccessibility from 'highcharts/modules/accessibility'
import nlMap from '@highcharts/map-collection/countries/nl/nl-all.topo.json'

import { ElectionMapSummary } from '../composables/ElectionMapSummary'
import type { ConstituencyMapSummary, ProvinceMapSummary, RegionLevel, RegionMapSummary } from '../types/ElectionMap'

function clamp(value: number, min: number, max: number) {
    return Math.min(max, Math.max(min, value))
}

function hexToRgb(hex: string): { r: number; g: number; b: number } | null {
    if (!hex) return null
    const raw = hex.trim().replace('#', '')
    const normalized = raw.length === 3 ? raw.split('').map((c) => c + c).join('') : raw
    if (!/^[0-9a-fA-F]{6}$/.test(normalized)) return null
    const int = parseInt(normalized, 16)
    return {
        r: (int >> 16) & 255,
        g: (int >> 8) & 255,
        b: int & 255
    }
}

function rgbToHex(r: number, g: number, b: number) {
    const to = (n: number) => clamp(Math.round(n), 0, 255).toString(16).padStart(2, '0')
    return `#${to(r)}${to(g)}${to(b)}`
}

function rgbToHsl(r: number, g: number, b: number) {
    const rn = r / 255
    const gn = g / 255
    const bn = b / 255
    const max = Math.max(rn, gn, bn)
    const min = Math.min(rn, gn, bn)
    const delta = max - min

    let h = 0
    let s = 0
    const l = (max + min) / 2

    if (delta !== 0) {
        s = delta / (1 - Math.abs(2 * l - 1))

        switch (max) {
            case rn:
                h = ((gn - bn) / delta) % 6
                break
            case gn:
                h = (bn - rn) / delta + 2
                break
            default:
                h = (rn - gn) / delta + 4
                break
        }

        h *= 60
        if (h < 0) h += 360
    }

    return { h, s, l }
}

function hslToRgb(h: number, s: number, l: number) {
    const c = (1 - Math.abs(2 * l - 1)) * s
    const x = c * (1 - Math.abs(((h / 60) % 2) - 1))
    const m = l - c / 2

    let rp = 0
    let gp = 0
    let bp = 0

    if (h >= 0 && h < 60) {
        rp = c
        gp = x
    } else if (h >= 60 && h < 120) {
        rp = x
        gp = c
    } else if (h >= 120 && h < 180) {
        gp = c
        bp = x
    } else if (h >= 180 && h < 240) {
        gp = x
        bp = c
    } else if (h >= 240 && h < 300) {
        rp = x
        bp = c
    } else {
        rp = c
        bp = x
    }

    return {
        r: (rp + m) * 255,
        g: (gp + m) * 255,
        b: (bp + m) * 255
    }
}

/**
 * Make an existing party color look a bit more cheerful/vivid on the map.
 * Keeps the hue, but boosts saturation + lightness within safe bounds.
 */
function toVibrantColor(hex: string): string {
    const rgb = hexToRgb(hex)
    if (!rgb) return hex

    const { h, s, l } = rgbToHsl(rgb.r, rgb.g, rgb.b)

    // Boost saturation and slightly lift lightness (but keep enough contrast for labels/borders).
    const boostedS = clamp(s * 1.25 + 0.18, 0, 1)
    const boostedL = clamp(l * 1.08 + 0.06, 0.18, 0.72)

    const out = hslToRgb(h, boostedS, boostedL)
    return rgbToHex(out.r, out.g, out.b)
}

const CONSTITUENCY_COORDS: Record<string, { lat: number; lon: number }> = {
    '1': { lat: 53.2194, lon: 6.5665 }, // Groningen
    '2': { lat: 53.2012, lon: 5.7999 }, // Leeuwarden
    '3': { lat: 52.9925, lon: 6.5622 }, // Assen
    '4': { lat: 52.5168, lon: 6.083 }, // Zwolle
    '5': { lat: 52.5185, lon: 5.4714 }, // Lelystad
    '6': { lat: 51.8425, lon: 5.8528 }, // Nijmegen
    '7': { lat: 51.9851, lon: 5.8987 }, // Arnhem
    '8': { lat: 52.0907, lon: 5.1214 }, // Utrecht
    '9': { lat: 52.3676, lon: 4.9041 }, // Amsterdam
    '10': { lat: 52.3874, lon: 4.6462 }, // Haarlem
    '11': { lat: 52.9563, lon: 4.7608 }, // Den Helder
    '12': { lat: 52.0705, lon: 4.3007 }, // 's-Gravenhage
    '13': { lat: 51.9244, lon: 4.4777 }, // Rotterdam
    '14': { lat: 51.8133, lon: 4.6901 }, // Dordrecht
    '15': { lat: 52.1601, lon: 4.497 }, // Leiden
    '16': { lat: 51.4988, lon: 3.6106 }, // Middelburg
    '17': { lat: 51.5555, lon: 5.0913 }, // Tilburg
    '18': { lat: 51.6978, lon: 5.3037 }, // 's-Hertogenbosch
    '19': { lat: 50.8514, lon: 5.6900 } // Maastricht
    // 20 (Bonaire) valt buiten de NL-kaart; bewust niet opgenomen.
}

// Enable Highcharts accessibility module (keyboard navigation, screen reader text).
// Some bundlers expose this module as a default export.
try {
    const initAccessibility =
        (HighchartsAccessibility as any)?.default ?? (HighchartsAccessibility as any)

    if (typeof initAccessibility === 'function') {
        initAccessibility(Highcharts as any)
    }
} catch (error) {
    // Do not hard-fail the entire route if the module init breaks.
    console.warn('Kon Highcharts accessibility module niet initialiseren', error)
}

const props = defineProps<{
    electionId: string
    level?: RegionLevel
    /**
     * Optional fixed chart height (useful for smaller previews, e.g. on Home page).
     * If omitted, the component will calculate a responsive height based on container width.
     */
    chartHeight?: number
}>()

const level = computed<RegionLevel>(() => props.level ?? 'province')

const emit = defineEmits<{
    (event: 'region-clicked', region: RegionMapSummary, level: RegionLevel): void
}>()

const containerRef = ref<HTMLElement | null>(null)
const chartRef = shallowRef<Highcharts.Chart | null>(null)

const resolvedChartHeight = ref(props.chartHeight ?? 560)

let renderRaf = 0
let renderRetries = 0

let resizeRaf = 0

function handleResize() {
    if (resizeRaf) {
        cancelAnimationFrame(resizeRaf)
    }

    resizeRaf = requestAnimationFrame(() => {
        if (!containerRef.value || !chartRef.value) return

        const containerWidth = containerRef.value.clientWidth || 0
        if (!containerWidth) return

        const nextHeight = props.chartHeight
            ? props.chartHeight
            : clamp(Math.round(containerWidth * 0.72), 380, 620)

        resolvedChartHeight.value = nextHeight
        chartRef.value.setSize(undefined as any, nextHeight, false)
        chartRef.value.reflow()
    })
}

const chartErrorMessage = ref('')

const { summary, isLoading, errorMessage } = ElectionMapSummary(toRef(props, 'electionId'), level)

function isProvinceSummary(item: RegionMapSummary): item is ProvinceMapSummary {
    return (item as ProvinceMapSummary).hcKey !== undefined
}

function isConstituencySummary(item: RegionMapSummary): item is ConstituencyMapSummary {
    return (item as ConstituencyMapSummary).constituencyNumber !== undefined
}

async function render() {
    if (!containerRef.value) return
    if (isLoading.value || errorMessage.value) return

    try {
        const selectedLevel: RegionLevel = level.value

        // Keep the map nicely inside its container on different screen sizes.
        // Highcharts needs an explicit height to avoid odd clipping/overflow.
        const containerWidth = containerRef.value.clientWidth || 0

        // When the container is toggled with v-show (or CSS/layout is still settling),
        // clientWidth can briefly be 0. If we render then, Highcharts creates a 0px-wide chart.
        if (!containerWidth) {
            if (renderRetries < 20) {
                renderRetries++
                if (renderRaf) cancelAnimationFrame(renderRaf)
                renderRaf = requestAnimationFrame(() => {
                    void render()
                })
            }
            return
        }

        renderRetries = 0

        const chartHeight = props.chartHeight
            ? props.chartHeight
            : clamp(Math.round(containerWidth * 0.72), 380, 620)

        resolvedChartHeight.value = chartHeight

        const options: Options = {
            chart: {
                map: nlMap as any,
                backgroundColor: 'transparent',
                height: chartHeight,
                spacing: [10, 10, 10, 10]
            },
            accessibility: {
                enabled: true,
                keyboardNavigation: {
                    enabled: true
                },
                point: {
                    descriptionFormatter: function (point: any) {
                        const custom = point?.options?.custom as ProvinceMapSummary | undefined
                        if (!custom) {
                            return point?.name ?? ''
                        }

                        const votes = custom.winningPartyVotes.toLocaleString('nl-NL')
                        const total = custom.totalVotes.toLocaleString('nl-NL')
                        return `${custom.regionName}. Koploper ${custom.winningPartyName} met ${votes} stemmen. Totaal ${total} stemmen.`
                    }
                }
            },
            title: {
                text: undefined
            },
            credits: {
                enabled: false
            },
            mapNavigation: {
                enabled: true,
                buttonOptions: {
                    verticalAlign: 'bottom'
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
                useHTML: true,
                formatter: function () {
                    const point = (this as any).point
                    const custom = point?.options?.custom as RegionMapSummary | undefined

                    if (!custom) {
                        return `<b>${point?.name ?? ''}</b>`
                    }

                    const votes = (custom.winningPartyVotes ?? 0).toLocaleString('nl-NL')
                    const total = (custom.totalVotes ?? 0).toLocaleString('nl-NL')

                    const subTitle = isConstituencySummary(custom)
                        ? `Kieskring ${custom.constituencyNumber}`
                        : 'Provincie'

                    return `
                      <div style="min-width:220px">
                        <div style="font-weight:800;margin-bottom:6px">${custom.regionName}</div>
                        <div style="color:#64748b;margin-bottom:6px">${subTitle}</div>
                        <div><span style="color:#64748b">Koploper</span>: <b>${custom.winningPartyName}</b></div>
                        <div><span style="color:#64748b">Stemmen koploper</span>: <b>${votes}</b></div>
                        <div><span style="color:#64748b">Totaal stemmen</span>: <b>${total}</b></div>
                      </div>
                    `
                }
            },
            plotOptions: {
                series: {
                    animation: false
                }
            },
            series: []
        }

        if (selectedLevel === 'province') {
            const provinceSeriesData = (summary.value as RegionMapSummary[])
                .filter(isProvinceSummary)
                .map((province) => ({
                    hcKey: province.hcKey,
                    value: province.totalVotes,
                    color: toVibrantColor(province.winningPartyColor),
                    custom: province
                }))

            options.series = [
                {
                    type: 'map',
                    name: 'Provincies',
                    mapData: nlMap as any,
                    joinBy: ['hc-key', 'hcKey'],
                    data: provinceSeriesData as any,
                    borderColor: '#ffffff',
                    borderWidth: 1,
                    nullColor: '#e2e8f0',
                    states: {
                        hover: {
                            brightness: 0.15
                        }
                    },
                    dataLabels: {
                        enabled: true,
                        format: '{point.name}',
                        style: {
                            fontSize: '10px',
                            fontWeight: '600',
                            textOutline: 'none'
                        }
                    },
                    point: {
                        events: {
                            click: function () {
                                const custom = (this as any)?.options?.custom as ProvinceMapSummary | undefined
                                if (custom) {
                                    emit('region-clicked', custom, 'province')
                                }
                            }
                        }
                    }
                }
            ]
        } else {
            const pointsData = (summary.value as RegionMapSummary[])
                .filter(isConstituencySummary)
                .map((constituency) => {
                    const key = String(constituency.constituencyNumber ?? '')
                        .trim()
                        .replace(/^0+(?!$)/, '')

                    const coords = CONSTITUENCY_COORDS[key]
                    if (!coords) return null
                    return {
                        name: constituency.regionName,
                        lat: coords.lat,
                        lon: coords.lon,
                        color: toVibrantColor(constituency.winningPartyColor),
                        custom: constituency
                    }
                })
                .filter(Boolean)

            options.series = [
                {
                    // Base map (grijs) zodat de gebruiker context heeft.
                    type: 'map',
                    name: 'Nederland',
                    mapData: nlMap as any,
                    data: [] as any,
                    borderColor: '#ffffff',
                    borderWidth: 1,
                    nullColor: '#e2e8f0',
                    dataLabels: {
                        enabled: false
                    },
                    states: {
                        hover: {
                            enabled: false
                        }
                    }
                },
                {
                    type: 'mappoint',
                    name: 'Kieskringen',
                    data: pointsData as any,
                    marker: {
                        radius: 6,
                        lineColor: '#0f172a',
                        lineWidth: 0.5
                    },
                    states: {
                        hover: {
                            brightness: 0.15
                        }
                    },
                    point: {
                        events: {
                            click: function () {
                                const custom = (this as any)?.options?.custom as ConstituencyMapSummary | undefined
                                if (custom) {
                                    emit('region-clicked', custom, 'constituency')
                                }
                            }
                        }
                    }
                }
            ]
        }

        chartRef.value?.destroy()
        chartRef.value = Highcharts.mapChart(containerRef.value, options)
    } catch (error) {
        console.error('Kon kaart niet renderen', error)
        chartErrorMessage.value = 'Kaart tonen is mislukt.'

        chartRef.value?.destroy()
        chartRef.value = null
    }
}

onMounted(render)

onMounted(() => {
    window.addEventListener('resize', handleResize)
})

watch(summary, () => {
    chartErrorMessage.value = ''
    render()
})

watch(
    () => props.chartHeight,
    (h) => {
        resolvedChartHeight.value = h ?? resolvedChartHeight.value
        render()
    }
)

watch(level, () => {
    render()
})

watch(errorMessage, (msg) => {
    if (msg) {
        chartRef.value?.destroy()
        chartRef.value = null
    }
})

watch(isLoading, (loading) => {
    if (loading) {
        chartRef.value?.destroy()
        chartRef.value = null
        return
    }

    // Loading finished: if there is no API error, render the latest summary.
    if (!errorMessage.value) {
        render()
    }
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    if (resizeRaf) {
        cancelAnimationFrame(resizeRaf)
    }
    if (renderRaf) {
        cancelAnimationFrame(renderRaf)
    }
    chartRef.value?.destroy()
    chartRef.value = null
})
</script>

<template>
  <section class="map-card">
    <header class="map-header">
      <div>
        <p class="eyebrow">Kaart</p>
        <h2>{{ (level ?? 'province') === 'province' ? 'Provincies' : 'Kieskringen' }}</h2>
      </div>
      <p class="caption">
        {{ (level ?? 'province') === 'province' ? 'Hover over een provincie voor de koploper' : 'Hover over een marker voor de koploper' }}
      </p>
    </header>
    <p v-if="isLoading" class="status">Kaart laden...</p>
    <p v-else-if="errorMessage || chartErrorMessage" class="status error">{{ errorMessage || chartErrorMessage }}</p>

    <div
      v-show="!isLoading && !errorMessage && !chartErrorMessage"
      ref="containerRef"
      class="map-container"
      :style="{ minHeight: `${resolvedChartHeight}px` }"
    ></div>
  </section>

</template>

<style scoped>
.map-card {
  padding: 24px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(148, 163, 184, 0.2);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.map-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 0.82rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #0f766e;
}

h2 {
  margin: 0;
  font-size: 1.6rem;
  color: #0f172a;
}

.caption {
  margin: 0;
  color: #64748b;
}

.status {
  margin: 0;
  padding: 14px 16px;
  border-radius: 16px;
  background: #e2e8f0;
  color: #0f172a;
}

.status.error {
  background: #fee2e2;
  color: #991b1b;
}

.map-container {
  width: 100%;
  max-width: 100%;
  overflow: hidden;
}

/* Force Highcharts/SVG to respect container width to avoid overflow in tight layouts (e.g. Home page). */
.map-container :deep(.highcharts-container),
.map-container :deep(svg) {
  width: 100% !important;
  height: 100% !important;
}

@media (max-width: 900px) {
  .map-card {
    padding: 20px;
  }
}

@media (max-width: 700px) {
  .map-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .map-container {
    min-height: 420px;
  }
}

@media (max-width: 480px) {
  .map-card {
    padding: 16px;
  }

  h2 {
    font-size: 1.3rem;
  }

  .map-container {
    min-height: 360px;
  }
}
</style>



