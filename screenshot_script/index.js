#!/usr/bin/env node
'use strict'

const fs = require('fs')
const puppeteer = require('puppeteer')
const imagesToPdf = require('images-to-pdf')

const argv = require('yargs')
  .option('ip', { type: 'string', alias: 'i', required: true })
  .option('port', { type: 'string', alias: 'p', default: '58035', required: true })
  .argv

function sleep (ms) {
  return new Promise(resolve => {
    setTimeout(resolve, ms)
  })
}

const urlBuilder = (ip, port, n) => `http://193.136.138.142:58000/index.html?IP=${ip}&PORT=${port}&SCRIPT=${n}`

async function main () {
  const browser = await puppeteer.launch({
    defaultViewport: {
      width: 800,
      height: 600,
      deviceScaleFactor: 2
    }
  })
  const page = await browser.newPage()
  const screenshots = []

  for (let i = 1; i <= 10; i++) {
    await page.goto(urlBuilder(argv.ip, argv.port, i), { waitUntil: 'networkidle2' })
    const screenshot = `screenshot-${i}.png`
    await page.screenshot({ path: screenshot, fullPage: true })
    screenshots.push(screenshot)
    await sleep(10000)
  }

  await imagesToPdf(screenshots, './screenshots.pdf')

  for (const screenshot of screenshots) {
    fs.unlinkSync(screenshot)
  }

  await browser.close()
}

main().catch(console.error)
