# Big Data Group28

## Name

Big-data Assessed Coursework Group 2

## Description

Goal:
The core goal of this pipeline is to take in a large set of text documents and a set of user defined queries, then for each query, rank the text documents by relevance for that query, as well as filter out any overly similar documents in the final ranking. The top 10 documents for each query should be returned as output.

For each document and query

1. remove stopwords and apply stemming for document (processor provided->textPreProcessor.java)
2. get DPH score(calculate relevance)(provided->DPHScorer.java) , connect with document and query
3. textual distance(reduce duplication)(provided->TextDistanceCalculator.java)
4. ranking and return top 10 documents for current query(provided->RankedResult.java)

本次任务的目标是熟悉使用 Apache Spark 设计、实现和性能测试大数据分析任务。您将需要设计和实现一个相对复杂的 Spark 应用程序。然后，在各种大小的数据上本地测试该应用程序的运行。最后，您将编写一个简短的报告，描述您的设计、设计决策，以及在适当的情况下批判您的设计。您将根据代码功能（是否产生预期结果）、代码质量（是否设计良好并遵循良好的软件工程实践）和效率（速度有多快并且是否使用资源高效）以及您提交的报告进行评估。

任务描述：

开发一个基于批处理的文本搜索和过滤管道。该管道的核心目标是接收大量的文本文档和一组用户定义的查询，然后对于每个查询，按相关性对文本文档进行排名，并过滤掉最终排名中的任何过于相似的文档。应返回每个查询的前 10 个文档作为输出。应对每个文档和查询进行处理以去除停用词（具有较少识别价值的词，例如“the”），并应用词干处理（将每个单词转换为其“词干”，一种较短的版本，有助于解决文档和查询之间的术语不匹配）。应使用 DPH 排名模型对文档进行评分。作为最后一个阶段，应分析每个查询的文档排名，以删除不必要的冗余（近似重复的文档），如果找到任何标题具有文本距离（使用提供的比较函数）小于 0.5 的文档对，则只应保留其中最相关的文档（基于 DPH 分数）。请注意，对于每个查询应返回 10 个文档，即使在去重过滤后也是如此。

任务分步：

- [x] 1.Spark 的读取和转换：加载给定的数据集，将数据集转换为 Dataset<NewsArticle> 和 Dataset<Query> 类型，以便进行下一步处理。

- [ ] 2.文本预处理：使用已经提供的静态文本预处理函数，将每个文档和查询进行处理以去除停用词并应用词干处理。

- [ ] 3.计算 DPH 分数：为每个<文档，术语>对计算 DPH 分数，需要计算以下统计信息：

- [ ] 4.术语在文档中的出现频率（计数）；

- [ ] 5.文档的长度（以术语计算）；

- [ ] 6.语料库中的平均文档长度（以术语计算）；

- [ ] 7.语料库中的文档总数；

- [ ] 8.术该术语在所有文档中的出现频率之和。
      使用这些统计信息计算 DPH 分数，然后计算<文档，查询>对的 DPH 分数。具体实现方法可以是编写一个静态 DPH 分数计算函数。

- [ ] 9.计算文本相似度：编写一个静态字符串距离函数，该函数接受两个字符串并计算它们之间的距离值（在 0-1 范围内）。该距离函数用于判断文本相似度是否达到要求。

- [ ] 10.进行文档排名：对每个查询，在所有文档中计算 DPH 分数，并根据分数进行排序。然后使用静态文本相似度函数过滤掉所有过于相似的文档，只保留最相关的文档。最后，选择每个查询的前 10 个文档，作为输出。

- [ ] 11.编写报告：编写一个短报告，描述您的解决方案的设计逻辑。您的报告应该包括每个 Spark 函数的部分，每个部分应该总结：

该函数的目标是什么；

该函数如何适应整个管道。

还应在最后一节中包括一段话，讨论为什么您认为您的解决方案是高效的，强调您为提高其效率所做的任何决策。您可能还希望强调您在实现中遇到的任何挑战以及如何克服这些挑战。

提交代码：您需要将代码上传到您的 git 存储库中，以便教练可以访问并评估您的工作。您需要按周提交代码并包含所有必要的注释和说明，以帮助他们更好地理解您的代码。
评分标准：

评分将取决于您的代码是否具有正确的功能、代码质量是否良好、效率和可伸缩性如何等因素。您的代码和提交的报告都将受到评估。具体来说，评分标准如下：

正确实现：12 分（部分正确实现将获得部分分数）；

代码质量 / 可读性：3 分；

代码文档：4 分；

效率 / 可扩展性：6 分。

## Badges

On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals

Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation

Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage

Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support

Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap

If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing

State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment

Show your appreciation to those who have contributed to the project.

## License

For open source projects, say how it is licensed.

## Project status

If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
