#!/usr/bin/env node
'use strict'

const puppeteer = require('puppeteer')

const argv = require('yargs')
  .option('ip', { type: 'string', alias: 'i', required: true })
  .option('port', { type: 'string', alias: 'p', default: '58035', required: true })
  .argv

function sleep (ms){
  return new Promise(resolve=>{
    setTimeout(resolve,ms)
  })
}

const urlBuilder = (ip, port, n) => `http://193.136.138.142:58000/index.html?IP=${ip}&PORT=${port}&SCRIPT=${n}`

async function main () {
  const browser = await puppeteer.launch()
  const page = await browser.newPage()

  for (let i = 1; i < 10; i++) {
    await page.goto(urlBuilder(argv.ip, argv.port, i), { waitUntil: 'networkidle2' })
    await page.screenshot({path: `screenshot-${i}.png`, fullPage: true })
    await sleep(10000)
  }

  await browser.close()
}

main().catch(console.error)
