<properties
   pageTitle="Data partitioning guidance | Microsoft Azure"
   description="Guidance upon how to separate partitions to be managed and accessed separately."
   services=""
   documentationCenter="na"
   authors="dragon119"
   manager="masimms"
   editor=""
   tags=""/>

<tags
   ms.service="best-practice"
   ms.devlang="na"
   ms.topic="article"
   ms.tgt_pltfrm="na"
   ms.workload="na"
   ms.date="12/20/2015"
   ms.author="masashin"/>

# Data partitioning guidance

![](media/best-practices-data-partitioning/pnp-logo.png)

## Overview

In many large-scale solutions, data is divided into separate partitions that can be managed and accessed separately. The partitioning strategy must be chosen carefully to maximize the benefits while minimizing adverse effects. Partitioning can help to improve scalability, reduce contention, and optimize performance. A side-benefit of partitioning is that can also provide a mechanism for dividing data by the pattern of use; you could archive older, more inactive (cold) data to cheaper data storage.