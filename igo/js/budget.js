/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

function budgetBootstrap () {
  enableKeybaordFor(document.getElementById('new-budget-name'))
  enableKeybaordFor(document.getElementById('new-budget-value'), false)
  enableKeybaordFor(document.getElementById('new-expense-name'))
  enableKeybaordFor(document.getElementById('new-expense-value'), false)
}

function getTextDate (date) {
  const year = date.getFullYear()
  const month = date.getMonth() < 10 ? `0${date.getMonth()}` : date.getMonth()
  return `${year} - ${month}`
}

function getBudgets () {
  return data.budgets.sort((a, b) => {
    if (a.active && !b.active) return 1
    if (b.active && !a.active) return 1
    if (a.date > b.date) return -1
    if (b.date > a.date) return 1
    return 0
  })
}

function isCurrentBudget (id) {
  return data.currentBudget && data.currentBudget.id === id
}

function getBudget (id) {
  if (isCurrentBudget(id)) {
    return data.currentBudget
  }

  for (const budget of data.budgets) {
    if (budget.id === id) {
      return budget
    }
  }
}

function fillBudgetList (screen) {
  const budgets = getBudgets()
  const content = screen.querySelector('.content')
  content.innerHTML = ''

  const makeElement = ({ name, date, id }, current) => {
    let el = getListTemplate(screen)

    if (current) {
      el.classList.add('current')
    }

    el.dataset.args = id
    el.querySelector('.name').innerHTML = name
    el.querySelector('.date').innerHTML = getTextDate(date)

    return el
  }

  if (data.currentBudget === null) {
    document.getElementById('new-budget-button').classList.remove('dn')
  } else {
    document.getElementById('new-budget-button').classList.add('dn')
    content.appendChild(makeElement(data.currentBudget, true))
  }

  for (const budget of budgets) {
    content.appendChild(makeElement(budget))
  }

  enableGoto(content)
}

function fillBudgetDesc (screen, id) {
  const { name, budget, expenses } = getBudget(id)
  const isCurrent = isCurrentBudget(id)

  for (const el of screen.querySelectorAll('.only-current')) {
    if (isCurrent) el.classList.remove('dn')
    else el.classList.add('dn')
  }

  updateScreenName(name)
  let spent = 0

  const expensesContainer = screen.querySelector('#budget-expenses .content')
  expensesContainer.innerHTML = ''

  for (const { name, value } of expenses) {
    let el = getListTemplate(screen)
    el.querySelector('.name').innerHTML = name
    el.querySelector('.value').innerHTML = value
    spent += value
    expensesContainer.appendChild(el)
  }

  screen.querySelector('#budget-spent').innerHTML = numberWithSpaces(spent)
  screen.querySelector('#budget-max').innerHTML = numberWithSpaces(budget)

  if (spent > budget) {
    screen.querySelector('#budget-bar div').classList.add('excess')
  } else {
    screen.querySelector('#budget-bar div').classList.remove('excess')
    screen.querySelector('#budget-bar div').style.width = `${spent / budget * 100}%`
  }
}

function validateNewBudget () {
  let name = document.getElementById('new-budget-name').value
  let value = parseFloat(document.getElementById('new-budget-value').value)

  if (name === '' || isNaN(value)) {
    // this shouldn't happen, but we never know!
    return
  }

  if (value <= 0) {
    return
  }

  return { name, value }
}

function onNewBudgetChange () {
  if (validateNewBudget()) {
    document.getElementById('new-budget-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-budget-submit').classList.add('disabled')
  }
}

function clearBudget () {
  document.getElementById('new-budget-name').value = ''
  document.getElementById('new-budget-value').value = ''
  document.getElementById('new-budget-submit').classList.add('disabled')
}

function createBudget () {
  if (data.currentBudget) {
    // this shouldn't happen, but we never know!
    return
  }

  let budget = validateNewBudget()
  if (!budget) {
    // this shouldn't happen, but we never know!
    return
  }

  confirmationBox({
    question: `Confirma a criação de um orçamento de valor ${numberWithSpaces(budget.value)} €?`,
    rightHandler: () => {
      window.data.currentBudget = {
        id: uuidv4(),
        name: budget.name,
        budget: budget.value,
        date: new Date(),
        expenses: []
      }

      runAndBack(clearBudget)
    }
  })
}

function clearExpense () {
  document.getElementById('new-expense-name').value = ''
  document.getElementById('new-expense-value').value = ''
  document.getElementById('new-expense-submit').classList.add('disabled')
}

function validateNewExpense () {
  let name = document.getElementById('new-expense-name').value
  let value = parseFloat(document.getElementById('new-expense-value').value)

  if (name === '' || isNaN(value) || value <= 0) {
    return
  }

  return { name, value }
}

function onNewExpenseChange () {
  if (validateNewExpense()) {
    document.getElementById('new-expense-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-expense-submit').classList.add('disabled')
  }
}

function createExpense () {
  let budget = validateNewExpense()
  if (!budget) {
    // shouldn't happen
    return
  }

  const spent = data.currentBudget.expenses.reduce((acc, e) => acc + e.value, 0)
  const overload = spent + budget.value > data.currentBudget.budget
  console.log(spent, budget.value)
  let question = `Deseja adicionar uma despesa no valor de ${numberWithSpaces(budget.value)} €?`
  if (overload) {
    question += ` Irá exceder o orçamento definido de ${numberWithSpaces(data.currentBudget.budget)} €.`
  }

  confirmationBox({
    question: question,
    rightClass: overload ? 'cancel' : 'ok',
    rightHandler: () => {
      data.currentBudget.expenses.push({ ...budget })
      runAndBack(clearExpense)
    }
  })
}

function deleteBudget () {
  confirmationBox({
    question: `Deseja remover o orçamento atual?`,
    rightClass: 'cancel',
    rightHandler: () => {
      data.currentBudget = null
      runAndBack()
    }
  })
}

function endBudget () {
  confirmationBox({
    question: `Deseja terminar a viagem atual?`,
    rightHandler: () => {
      data.budgets.push(data.currentBudget)
      data.currentBudget = null
      runAndBack()
    }
  })
}

function simulatePayment (el) {
  const what = el.querySelector('input[type="text"]').value
  if (!what) return
  const howMuch = el.querySelector('input[type="number"]').value
  if (isNaN(howMuch)) return

  confirmationBox({
    question: `Confirma a compra no valor de ${numberWithSpaces(howMuch)} € no ${what}?`,
    rightHandler: () => {
      if (!data.currentBudget) {
        return
      }

      data.currentBudget.expenses.push({
        name: what,
        value: howMuch
      })
    }
  })
}
